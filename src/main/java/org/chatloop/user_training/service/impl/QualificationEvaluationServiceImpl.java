package org.chatloop.user_training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.model.content.UserQualification;
import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.model.progress.CourseResult;
import org.chatloop.user_training.model.progress.EarnedQualifications;
import org.chatloop.user_training.model.progress.EvaluationResult;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.CourseProgressionService;
import org.chatloop.user_training.service.EarnedQualificationsService;
import org.chatloop.user_training.service.QualificationEvaluationService;
import org.chatloop.user_training.spring.events.*;
import org.springframework.context.event.EventListener;

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

    public QualificationEvaluationServiceImpl(CdnService cdnService, CourseProgressionService progressionService, EarnedQualificationsService earnedQualificationsService) {
        this.cdnService = cdnService;
        this.progressionService = progressionService;
        this.earnedQualificationsService = earnedQualificationsService;
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
        return runEvaluation(progressionService::progressForCourse);
    }

    /**
     * Evaluates course progress computes earned qualifications. This is done in
     * the following steps:
     * - retrieves all available qualifications
     * - for each qualification, look at the course progress requirements and fetches course progression using the
     *   specified {@code courseProgressFinder}
     * - evaluates which users satisfy all requirements and stores them in the database
     * @param courseProgressFinder a {@link Function} which is used to fetch course progress
     * @return EvaluationResult
     */
    private EvaluationResult runEvaluation(Function<String, Stream<CourseProgress>> courseProgressFinder) {
        EvaluationResult result = new EvaluationResult();
        List<UserQualification> userQualifications = fetchExistingQualifications();
        log.debug("Found {} existing qualifications which users may qualify for.", userQualifications.size());
        if (!userQualifications.isEmpty()) {
            for (UserQualification userQualification : userQualifications) {
                String qualificationName = userQualification.getName();
                List<String> requiredCoursesIds = new ArrayList<>(userQualification.getCourseIds());
                log.debug("Evaluating who qualifies for qualification: {}, which requires course(s): {} to be finished.", qualificationName, String.join(", ", requiredCoursesIds));
                if (!requiredCoursesIds.isEmpty()) {
                    String firstCourseId = requiredCoursesIds.remove(0);
                    log.debug("Fetching progress in course {} for all users.", firstCourseId);
                    courseProgressFinder.apply(firstCourseId)
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
                                    log.debug("User {} qualifies for qualification {}.", candidateId, qualificationName);
                                    result.addQualifyingUser(qualificationName, candidateId);
                                }
                    });
                }
            }
        }

        return result;
    }

    /**
     * Listens to a {@link ContentRelatedChangeEvent} which indicates that the underlying content model has changed.
     * When this happens, we need to (re)evaluate the earned qualifications for all users, update the
     * database and notify downstream services about the change delta.
     * @param contentRelatedChangeEvent the {@link ContentRelatedChangeEvent}
     */
    @EventListener
    public void onApplicationEvent(ContentRelatedChangeEvent contentRelatedChangeEvent) {
        log.debug("Received {}", contentRelatedChangeEvent.getClass().getSimpleName());

        var previouslyEarnedQualifications = earnedQualificationsService
                .fetchAllEarnedQualifications()
                .collect(Collectors.toMap(EarnedQualifications::getUserId, e -> e));
        log.debug("A total of {} users have previously earned a qualification.", previouslyEarnedQualifications.size());

        var newlyEvaluatedEarnedQualifications = evaluateAllUsers()
                .asEarnedQualifications()
                .collect(Collectors.toMap(EarnedQualifications::getUserId, e -> e));
        log.debug("Evaluated that {} users earned at least one qualification.", newlyEvaluatedEarnedQualifications.size());

        processQualificationChanges(previouslyEarnedQualifications, newlyEvaluatedEarnedQualifications);
    }

    /**
     * Listens to a {CourseRelatedUserEvent} which indicates a user has either passed or reset a single course.
     * As this could potentially alter the earned qualifications of that particular user, we need to (re)evaluate
     * the earned qualifications, update the database and notify downstream services about the change delta.
     * @param courseRelatedUserEvent the {@link CoursePassedEvent}
     */
    @EventListener
    public void onApplicationEvent(CourseRelatedUserEvent courseRelatedUserEvent) {
        log.debug("Received {}", courseRelatedUserEvent.getClass().getSimpleName());

        String userId = courseRelatedUserEvent.getUserId();
        var previouslyEarnedQualifications = earnedQualificationsService
                .fetchEarnedQualifications(userId)
                .map(earnedQualifications -> Collections.singletonMap(userId, earnedQualifications))
                .orElse(Collections.emptyMap());
        log.debug("User {} has previously earned {} qualification(s).", userId, previouslyEarnedQualifications.isEmpty() ? 0 : previouslyEarnedQualifications.get(userId).getQualifications().size());

        var newlyEvaluatedEarnedQualifications = evaluateUsers(userId)
                .asEarnedQualifications()
                .collect(Collectors.toMap(EarnedQualifications::getUserId, e -> e));
        log.debug("After running (re)evaluation, user {} earned {} qualification.", userId, newlyEvaluatedEarnedQualifications.size());

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
        log.debug("{} users no longer qualify for at least one qualification.", toRemove.size());

        // find users whose qualifications have changed
        for (String userId : newlyEvaluatedEarnedQualifications.keySet()) {
            var now = newlyEvaluatedEarnedQualifications.get(userId);
            var before = previouslyEarnedQualifications.get(userId);
            // if the user didn't have any previous qualifications or they were different
            if (before == null || !now.getQualifications().containsAll(before.getQualifications()) || !before.getQualifications().containsAll(now.getQualifications())) {
                changes.add(now);
            }
        }
        log.debug("For {} users qualifications have changed.", changes.size());

        // handle removals
        earnedQualificationsService.removeEarnedQualifications(toRemove.toArray(new EarnedQualifications[0]));

        // handle changes
        earnedQualificationsService.storeEarnedQualifications(changes.toArray(new EarnedQualifications[0]));

        // notify downstream
        log.debug("Notifying downstream services about changes to qualifications.");
    }

    /**
     * Returns a list of existing {@link UserQualification}s.
     * @return List<UserQualification>
     */
    protected List<UserQualification> fetchExistingQualifications() {
        return cdnService.fetchUserQualifications();
    }
}
