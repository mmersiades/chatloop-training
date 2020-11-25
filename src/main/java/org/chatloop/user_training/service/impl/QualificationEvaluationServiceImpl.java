package org.chatloop.user_training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.model.content.UserQualification;
import org.chatloop.user_training.model.progress.*;
import org.chatloop.user_training.model.progress.QualificationChangeSummary.EarnedUserQualificationTo;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.CourseProgressionService;
import org.chatloop.user_training.service.EarnedQualificationsService;
import org.chatloop.user_training.service.QualificationEvaluationService;
import org.chatloop.user_training.spring.events.ContentRelatedChangeEvent;
import org.chatloop.user_training.spring.events.CoursePassedEvent;
import org.chatloop.user_training.spring.events.CourseRelatedUserEvent;
import org.chatloop.user_training.util.PageContext;
import org.chatloop.user_training.web.ChatloopMonolithFacade;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@inheritDoc}
 */
@Slf4j
public class QualificationEvaluationServiceImpl implements QualificationEvaluationService {

    private final CdnService cdnService;
    private final CourseProgressionService progressionService;
    private final EarnedQualificationsService earnedQualificationsService;
    private final ChatloopMonolithFacade chatloopMonolithFacade;

    public QualificationEvaluationServiceImpl(CdnService cdnService, CourseProgressionService progressionService, EarnedQualificationsService earnedQualificationsService, ChatloopMonolithFacade facade) {
        this.cdnService = cdnService;
        this.progressionService = progressionService;
        this.earnedQualificationsService = earnedQualificationsService;
        this.chatloopMonolithFacade = facade;
    }

    /**
     * @inheritDoc
     */
    @Override
    public EvaluationResult evaluateUsers(String... userIds) {
        log.info("Starting to evaluate qualifications for the following users: {}.", Arrays.toString(userIds));
        return runEvaluation((courseId) -> progressionService.userProgressForCourse(courseId, userIds));
    }

    /**
     * @inheritDoc
     */
    @Override
    public EvaluationResult evaluateAllUsers() {
        log.info("Starting to evaluate qualifications for all users.");

        EvaluationResult totalResult = new EvaluationResult();

        List<UserQualification> userQualifications = fetchExistingQualifications();
        log.info("Found {} existing qualifications which users may qualify for.", userQualifications.size());
        if (!userQualifications.isEmpty()) {
            for (UserQualification userQualification : userQualifications) {
                // for each potential qualification, fetch course progess in pages of 500 items
                final PageContext pageContext = new PageContext(userQualification.getName(), 500);
                final Function<String, Stream<CourseProgress>> sliceCourseProgressFinder = courseId -> {
                    Pageable pageable = pageContext.getPageable();
                    String qualificationName = pageContext.getContextName();
                    log.info("Requesting next progress slice for course {} to check qualification {}", courseId, qualificationName);

                    Slice<CourseProgress> currentSlice = progressionService.courseProgress(courseId, pageable);
                    pageContext.setSlice(currentSlice);

                    int number = currentSlice.getNumber();
                    int numberOfElements = currentSlice.getNumberOfElements();
                    int size = currentSlice.getSize();
                    log.info("Slice #{}, Elements: {}, Size: {}, Qualification: {}", number, numberOfElements, size, qualificationName);

                    if (currentSlice.hasNext()) {
                        pageContext.setPageable(currentSlice.nextPageable());
                    } else {
                        pageContext.setPageable(null);
                    }
                    List<CourseProgress> items = currentSlice.getContent();
                    return items.stream();
                };

                while (pageContext.getPageable() != null) {
                    EvaluationResult sliceResult = runEvaluation(userQualification, sliceCourseProgressFinder);
                    totalResult.merge(sliceResult);
                }
            }
        }

        return totalResult;
    }

    /**
     * Evaluates course progress computes earned qualifications. This is done in the following steps:
     * - retrieves all available qualifications
     * - for each qualification, look at the course progress requirements and fetches course progression using the
     *   specified {@code courseProgressFinder}
     * - evaluates which users satisfy all requirements and stores them in the database
     * @param courseProgressFinder a {@link Function} which is used to fetch course progress
     * @return EvaluationResult
     */
    private EvaluationResult runEvaluation(Function<String, Stream<CourseProgress>> courseProgressFinder) {
        EvaluationResult totalResult = new EvaluationResult();
        List<UserQualification> userQualifications = fetchExistingQualifications();
        log.info("Found {} existing qualifications which users may qualify for.", userQualifications.size());
        if (!userQualifications.isEmpty()) {
            for (UserQualification userQualification : userQualifications) {
                EvaluationResult singleQualificationResult = runEvaluation(userQualification, courseProgressFinder);
                totalResult.merge(singleQualificationResult);
            }
        }
        return totalResult;
    }

    /**
     * For the given {@link UserQualification} this functions evaluates the course progress and computes who
     * has earned this qualifications. This is done in the following steps:
     * - look at the course progress requirements and fetches course progression using the
     *   specified {@code courseProgressFinder}
     * - evaluates which users satisfy all requirements and stores them in the database
     * @param userQualification a {@link UserQualification}
     * @param courseProgressFinder a {@link Function} which is used to fetch course progress
     * @return EvaluationResult
     */
    private EvaluationResult runEvaluation(UserQualification userQualification, Function<String, Stream<CourseProgress>> courseProgressFinder) {
        EvaluationResult result = new EvaluationResult();
        String qualificationName = userQualification.getName();
        List<String> requiredCoursesIds = new ArrayList<>(userQualification.getCourseIds());
        log.info("Evaluating who qualifies for qualification: {}, which requires course(s): {} to be finished.", qualificationName, String.join(", ", requiredCoursesIds));
        if (!requiredCoursesIds.isEmpty()) {
            String firstCourseId = requiredCoursesIds.remove(0);
            log.info("Fetching course progress in course {}.", firstCourseId);
            courseProgressFinder.apply(firstCourseId)
                    .parallel()
                    .filter(courseProgress -> courseProgress.getCourseResult() == CourseResult.COURSE_PASSED)
                    .forEach(courseProgress -> {
                        String candidateId = courseProgress.getUserId();
                        log.debug("User {} has passed course {}.", candidateId, firstCourseId);

                        boolean satisfiesAll = true;
                        for (String requiredCourseId : requiredCoursesIds) {
                            log.debug("Checking if user {} has also passed course {}.", candidateId, requiredCourseId);
                            Optional<CourseProgress> otherCourseProgress = progressionService.singleUserProgressForCourse(candidateId, requiredCourseId);
                            satisfiesAll = otherCourseProgress
                                    .map(other -> other.getCourseResult() == CourseResult.COURSE_PASSED)
                                    .orElse(false);
                            if (!satisfiesAll) {
                                log.debug("User {} has not passed that course.", candidateId);
                                break;
                            }
                        }

                        if (satisfiesAll) {
                            log.info("User {} qualifies for qualification {}.", candidateId, qualificationName);
                            result.addQualifyingUser(qualificationName, candidateId);
                        }
                    });
        }
        return result;
    }

    /**
     * Processes a {@link ContentRelatedChangeEvent} indicating that the underlying content model has changed.
     * When this happens, we need to (re)evaluate the earned qualifications for all users, update the
     * database and notify downstream services about the change delta.
     * @param contentRelatedChangeEvent the {@link ContentRelatedChangeEvent}
     */
    @EventListener
    public void onContentRelatedChangeEvent(ContentRelatedChangeEvent contentRelatedChangeEvent) {
        log.info("Received {}", contentRelatedChangeEvent.getClass().getSimpleName());

        var previouslyEarnedQualifications = earnedQualificationsService
                .fetchAllEarnedQualifications()
                .collect(Collectors.toMap(EarnedQualifications::getUserId, e -> e));
        log.info("A total of {} users have previously earned a qualification.", previouslyEarnedQualifications.size());

        var newlyEvaluatedEarnedQualifications = evaluateAllUsers()
                .asEarnedQualifications()
                .collect(Collectors.toMap(EarnedQualifications::getUserId, e -> e));
        log.info("Evaluated that {} users earned at least one qualification.", newlyEvaluatedEarnedQualifications.size());

        processQualificationChanges(previouslyEarnedQualifications, newlyEvaluatedEarnedQualifications);
    }

    /**
     * Listens to a {CourseRelatedUserEvent} which indicates a user has either passed or reset a single course.
     * As this could potentially alter the earned qualifications of that particular user, we need to (re)evaluate
     * the earned qualifications, update the database and notify downstream services about the change delta.
     * @param courseRelatedUserEvent the {@link CoursePassedEvent}
     */
    @EventListener
    public void onCourseRelatedUserEvent(CourseRelatedUserEvent courseRelatedUserEvent) {
        log.info("Received {}", courseRelatedUserEvent.getClass().getSimpleName());

        String userId = courseRelatedUserEvent.getUserId();
        var previouslyEarnedQualifications = earnedQualificationsService
                .fetchEarnedQualifications(userId)
                .map(earnedQualifications -> Collections.singletonMap(userId, earnedQualifications))
                .orElse(Collections.emptyMap());
        log.info("User {} has previously earned {} qualification(s).", userId, previouslyEarnedQualifications.isEmpty() ? 0 : previouslyEarnedQualifications.get(userId).getQualifications().size());

        var newlyEvaluatedEarnedQualifications = evaluateUsers(userId)
                .asEarnedQualifications()
                .collect(Collectors.toMap(EarnedQualifications::getUserId, e -> e));
        log.info("After running (re)evaluation, user {} earned {} qualification.", userId, newlyEvaluatedEarnedQualifications.size());

        processQualificationChanges(previouslyEarnedQualifications, newlyEvaluatedEarnedQualifications);
    }

    /**
     * Given previously earned qualifications, and a collection of qualifications after (re)evaluation, this function
     * performs all necessary database update and notifies downstream services about the change delta.
     * @param previouslyEarnedQualifications collection of previously earned qualifications
     * @param newlyEvaluatedEarnedQualifications collection of evaluated qualifications
     */
    private void processQualificationChanges(Map<String, EarnedQualifications> previouslyEarnedQualifications, Map<String, EarnedQualifications> newlyEvaluatedEarnedQualifications) {
        var toRemove = new ArrayList<EarnedQualifications>();
        var changes = new ArrayList<EarnedQualifications>();

        // find users who lost all their earned qualifications
        for (String userId : previouslyEarnedQualifications.keySet()) {
            if (!newlyEvaluatedEarnedQualifications.containsKey(userId)) {
                toRemove.add(previouslyEarnedQualifications.get(userId));
            }
        }
        log.info("{} users no longer qualify for at least one qualification.", toRemove.size());

        // find users whose qualifications have changed
        for (String userId : newlyEvaluatedEarnedQualifications.keySet()) {
            var now = newlyEvaluatedEarnedQualifications.get(userId);
            var before = previouslyEarnedQualifications.get(userId);
            // if the user didn't have any previous qualifications or they were different
            if (before == null || !now.getQualifications().containsAll(before.getQualifications()) || !before.getQualifications().containsAll(now.getQualifications())) {
                changes.add(now);
            }
        }
        log.info("For {} users qualifications have changed.", changes.size());

        // handle removals
        earnedQualificationsService.removeEarnedQualifications(toRemove.toArray(new EarnedQualifications[0]));

        // handle changes
        earnedQualificationsService.storeEarnedQualifications(changes.toArray(new EarnedQualifications[0]));

        // notify downstream
        log.info("Notifying downstream services about changes to qualifications.");

        if (!toRemove.isEmpty() || !changes.isEmpty()) {
            var removals = toRemove.stream().map(EarnedQualifications::getUserId).collect(Collectors.toList());
            var changeList = changes.stream().map(item -> new EarnedUserQualificationTo(item.getUserId(), new ArrayList<>(item.getQualifications()))).collect(Collectors.toList());
            QualificationChangeSummary summary = new QualificationChangeSummary(removals, changeList);
            chatloopMonolithFacade.notifyEarnedQualificationsChanges(summary);
        }
    }

    /**
     * Returns a list of existing {@link UserQualification}s.
     * @return List<UserQualification>
     */
    protected List<UserQualification> fetchExistingQualifications() {
        return cdnService.fetchUserQualifications();
    }
}
