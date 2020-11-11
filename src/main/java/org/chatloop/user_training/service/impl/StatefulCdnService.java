package org.chatloop.user_training.service.impl;

import com.contentful.java.cda.*;
import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.model.content.ContentTypes;
import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.model.content.UserQualification;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;
import org.chatloop.user_training.spring.events.ContentUpdatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Will cache Contentful data locally in JVM heap.
 *
 * Intentionally not using streams API so that it is easier to troubleshoot, given
 * that Contentful is external service not under our control. Smart/fast tricks are
 * not intentionally used since amount of data is very low.
 *
 * TODO on cache miss trigger resync
 */
@Slf4j
@Service
public class StatefulCdnService implements CdnService {

    @Value("${contentful.spaceId}")
    private String spaceId;

    @Value("${contentful.envId}")
    private String envId;

    @Value("${contentful.deliveryToken}")
    private String deliveryToken;

    @Value("${contentful.log.fetched}")
    private boolean logFetched;

    private CDAClient client;

    private SynchronizedSpace syncedSpace;

    private final Transformer transformer;

    private final ChatloopApplicationEventPublisher applicationEventPublisher;

    public StatefulCdnService(Transformer transformer, ChatloopApplicationEventPublisher applicationEventPublisher) {
        this.transformer = transformer;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    // TODO double check, not much in their docs about thread safety
    private final ReadWriteLock spaceLock = new ReentrantReadWriteLock();

    /**
     * Only for crawler.
     */
    public CDAClient getClient() {
        return client;
    }

    /**
     * Only for crawler.
     */
    public SynchronizedSpace getSyncedSpace() {
        return syncedSpace;
    }

    @PostConstruct
    public void init() {
        this.client = createClient();

        log.info("Connected to Contentful space {}, environment {}", spaceId, envId);

        syncSchema();
        this.syncedSpace = syncData();

        log.info("Initial sync of space {} complete, data is now cached in JVM", spaceId);
        logFetchedContent();

        ApplicationEvent event = new ContentUpdatedEvent(StatefulCdnService.class);
        this.applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void markCacheStale() {
        log.info("Cached marked as stale, starting re-sync of space {}", spaceId);
        spaceLock.writeLock().lock();
        try {
            SyncQuery syncQuery = this.client.sync(); // replaces below
            syncedSpace = syncQuery.fetch();
            /**
             * This call may lead to missing pointer/edge between program and course. So even
             * though they are linked in Contentful, we observe it via their Java client API
             * as missing. Given very low amount of data we work with, as well as expected low
             * rate of change on Contentful, doing full sync should be fine from perf perspective.
             * Issue was happening when course is repeatable unpublished/published. Unfortunately
             * their Javadoc is very limited. If performance requirements are dramatically changed
             * then this can be revisited, including contacting their support for help. For now single
             * write lock ensures we don't flood Contentful with large number of requests.
             */
//            SyncQuery syncQuery = client.sync(syncedSpace);
//            syncedSpace = syncQuery.fetch();
            logFetchedContent();
        } finally {
            spaceLock.writeLock().unlock();
        }
        log.info("Re-sync of space {} complete", spaceId);
    }

    @Override
    public List<UserProgram> fetchUserPrograms() {
        spaceLock.readLock().lock();
        try {
            List<UserProgram> programs = new ArrayList<>();
            for (CDAEntry entry : syncedSpace.entries().values()) {
                if (ContentTypes.PROGRAM.supports(entry)) {
                    programs.add(transformer.toUserProgram(entry, true));
                }
            }
            return programs;
        } finally {
            spaceLock.readLock().unlock();
        }
    }

    @Override
    public List<UserQualification> fetchUserQualifications() {
        spaceLock.readLock().lock();
        try {
            List<UserQualification> qualifications = new ArrayList<>();
            for (CDAEntry entry : syncedSpace.entries().values()) {
                if (ContentTypes.QUALIFICATION.supports(entry)) {
                    qualifications.add(transformer.toUserQualification(entry));
                }
            }
            return qualifications;
        } finally {
            spaceLock.readLock().unlock();
        }
    }

    @Override
    public Optional<UserProgram> fetchProgramById(String programId) {
        spaceLock.readLock().lock();
        try {
            CDAEntry entry = syncedSpace.entries().get(programId);
            if (entry == null) {
                return Optional.empty();
            }
            log.info("Resolved program ID {} to {}", programId, entry);
            return Optional.of(transformer.toUserProgram(entry, true));
        } finally {
            spaceLock.readLock().unlock();
        }
    }

    @Override
    public Optional<UserCourse> fetchCourseById(String courseId) {
        spaceLock.readLock().lock();
        try {
            CDAEntry entry = syncedSpace.entries().get(courseId);
            if (entry == null) {
                log.warn("Course {} not found in space {}, env {} ", courseId, spaceId, envId);
                return Optional.empty();
            }
            log.info("Resolved course ID {} to {}", courseId, entry);
            UserCourse userCourse = transformer.toUserCourse(entry);
            return Optional.of(userCourse);
        } finally {
            spaceLock.readLock().unlock();
        }
    }

    /**
     * Runs a bunch of validations against the Contentful schema. For instance it checks,
     * that all required content types exist.
     */
    protected void syncSchema() {
        FetchQuery<CDAContentType> query = client.fetch(CDAContentType.class);
        CDAArray types = query.all();

        Set<String> detectedContentTypes = types.items().stream()
                .map(CDAResource::id).collect(Collectors.toSet());

        for (String requiredType : REQUIRED_TYPEs) {
            if (!detectedContentTypes.contains(requiredType)) {
                String message = String.format("Required content type %s not found in space %s, env %s, found %s",
                        requiredType, spaceId, envId, detectedContentTypes);
                throw new IllegalStateException(message);
            }
        }
        log.info("Schema synchronized, detected content types {}", detectedContentTypes);
    }

    /**
     * Fetches and returns a {@link SynchronizedSpace} from Contentful.
     */
    protected SynchronizedSpace syncData() {
        SyncQuery syncQuery = this.client.sync();
        return syncQuery.fetch();
    }

    /**
     * Initializes the {@link CDAClient} for communication with Contentful.
     */
    protected CDAClient createClient() {
        return CDAClient.builder()
                .setSpace(spaceId)
                .setEnvironment(envId)
                .setToken(deliveryToken)
                .build();
    }

    private void logFetchedContent() {
        if (!logFetched) {
            return;
        }
        int count = 0;

        Set<String> types = new HashSet<>(ContentTypes.allContentTypes());
        for (CDAEntry entry : syncedSpace.entries().values()) {
            CDAContentType cdnType = entry.contentType();
            if (types.contains(cdnType.name())) {
                ++count;
                log.info("Fetched {} / {} / {}", cdnType.name(), entry.getField("name"), entry.id());
            }
        }
        log.info("Fetched total {} entries", count);
    }

    private static final Set<String> REQUIRED_TYPEs = new HashSet<>(ContentTypes.allContentTypes());

//    private Program getParentFor(Course course) {
//        for(Program program : this.programs) {
//            if(program.containsCourse(course)) {
//                Program parent = program.copyWithoutChildren();
//                parent.addCourse(course);
//                return parent;
//            }
//        }
//        throw new IllegalStateException();
//    }
}
