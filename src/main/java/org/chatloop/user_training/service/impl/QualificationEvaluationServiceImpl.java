package org.chatloop.user_training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.model.content.UserQualification;
import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.model.progress.CourseResult;
import org.chatloop.user_training.model.progress.EvaluationResult;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.CourseProgressionService;
import org.chatloop.user_training.service.QualificationEvaluationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@inheritDoc}
 */
@Slf4j
public class QualificationEvaluationServiceImpl implements QualificationEvaluationService {

    private final CdnService cdnService;
    private final CourseProgressionService progressionService;

    public QualificationEvaluationServiceImpl(CdnService cdnService, CourseProgressionService progressionService) {
        this.cdnService = cdnService;
        this.progressionService = progressionService;
    }

    /**
     * Evaluates the course progress for all users and updates their earned qualifications. This is done in
     * the following steps:
     * - retrieves all available qualifications
     * - for each qualification looks at the course progress requirements and fetches user course progression
     * - evaluates which users satisfy all requirements and stores them in the database
     * @return EvaluationResult
     */
    @Override
    public EvaluationResult evaluateAllUsers() {
        log.info("Starting to evaluate qualifications for all users.");
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
                    progressionService.userProgressForCourse(firstCourseId)
                            .filter(courseProgress -> courseProgress.getCourseResult() == CourseResult.COURSE_PASSED)
                            .forEach(courseProgress -> {
                                String candidateId = courseProgress.getUserId();
                                log.debug("User {} has passed course {}.", candidateId, firstCourseId);

                                boolean satisfiesAll = true;
                                for (String requiredCourseId : requiredCoursesIds) {
                                    log.debug("Checking if user {} has also passed course {}.", candidateId, requiredCourseId);
                                    Optional<CourseProgress> otherCourseProgress = progressionService.userProgressForCourse(candidateId, requiredCourseId);
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
     * Returns a list of existing {@link UserQualification}s.
     * @return List<UserQualification>
     */
    protected List<UserQualification> fetchExistingQualifications() {
        return cdnService.fetchUserQualifications();
    }
}
