package org.chatloop.user_training.util;

import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.model.progress.CourseResult;
import org.chatloop.user_training.model.progress.EarnedQualifications;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Fixture {
    private static final String DEFAULT_SPACE_ID = "space";
    private static final String DEFAULT_ENVIRONMENT_ID = "environment";

    /**
     * Creates and returns a new {@link CourseProgress} object.
     * @param userId the user id of the user which this {@link CourseProgress} belongs to
     * @param courseId the course if of the course which is progressed
     * @return {@link CourseProgress}
     */
    public static CourseProgress courseProgressFor(String userId, String courseId) {
        return Fixture.courseProgressFor(userId, courseId, CourseResult.COURSE_PASSED);
    }

    /**
     * Creates and returns a new {@link CourseProgress} object.
     * @param userId the user id of the user which this {@link CourseProgress} belongs to
     * @param courseId the course if of the course which is progressed
     * @param courseResult sets the {@link CourseResult} inside the {@link CourseProgress}
     * @return {@link CourseProgress}
     */
    public static CourseProgress courseProgressFor(String userId, String courseId, CourseResult courseResult) {
        CourseProgress progress = new CourseProgress();
        progress.setSpaceId(DEFAULT_SPACE_ID);
        progress.setEnvId(DEFAULT_ENVIRONMENT_ID);
        progress.setUserId(userId);
        progress.setCourseId(courseId);
        progress.setCourseResult(courseResult);
        return progress;
    }

    /**
     * Creates and returns a new {@link EarnedQualifications} object.
     * @param userId the user id of the user who earned the qualifications
     * @param qualifications the collections of qualifications which were earned
     * @return {@link EarnedQualifications}
     */
    public static EarnedQualifications earnedQualifications(String userId, String... qualifications) {
        EarnedQualifications earnedQualifications = new EarnedQualifications();
        earnedQualifications.setUserId(userId);
        earnedQualifications.setQualifications(
            Arrays.stream(qualifications).collect(Collectors.toSet())
        );
        return earnedQualifications;
    }
}
