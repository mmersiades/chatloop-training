package org.chatloop.user_training.service;

import org.chatloop.user_training.model.progress.CourseProgress;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * This {@code Service} wraps all logic related to the users progression through a course.
 */
public interface CourseProgressionService {

    /**
     * Returns a {@link Stream} containing items denoting the progress of individual users
     * in the course having the specified {@code courseId}.
     * @param courseId the id of a course
     * @return {@link Stream}
     */
    Stream<CourseProgress> userProgressForCourse(String courseId);

    /**
     * Potentially returns the {@link CourseProgress} for the specified user in the course having the
     * specified {@code courseId}.
     * @param userId the id of a user
     * @param courseId the id of a course
     * @return Optional<CourseProgress>
     */
    Optional<CourseProgress> userProgressForCourse(String userId, String courseId);
}
