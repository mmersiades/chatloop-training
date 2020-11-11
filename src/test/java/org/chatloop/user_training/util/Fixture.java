package org.chatloop.user_training.util;

import org.chatloop.user_training.model.progress.CourseProgress;

public class Fixture {
    private static final String DEFAULT_SPACE_ID = "space";
    private static final String DEFAULT_ENVIRONMENT_ID = "environment";

    /**
     * Creates and returns a new {@link CourseProgress} objects.
     * @param userId the user id of the user which this {@link CourseProgress} belongs to
     * @param courseId the course if of the course which is progressed
     * @return {@link CourseProgress}
     */
    public static CourseProgress courseProgressFor(String userId, String courseId) {
        CourseProgress progress = new CourseProgress();
        progress.setSpaceId(DEFAULT_SPACE_ID);
        progress.setEnvId(DEFAULT_ENVIRONMENT_ID);
        progress.setUserId(userId);
        progress.setCourseId(courseId);
        return progress;
    }
}
