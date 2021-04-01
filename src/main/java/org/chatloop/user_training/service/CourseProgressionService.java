package org.chatloop.user_training.service;

import org.chatloop.user_training.model.progress.CourseProgress;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * This {@code Service} wraps all logic related to the users progression through a course.
 */
public interface CourseProgressionService {

    /**
     * Returns a {@link Stream} containing elements denoting the progress of all users
     * in the course having the specified {@code courseId}.
     * @param courseId the id of a course
     * @return {@link Stream}
     */
    Stream<CourseProgress> progressForCourse(String courseId);

    /**
     * Returns a {@link Slice} containing {@link CourseProgress} elements denoting the progress of all users
     * in the course having the specified {@code courseId}.
     * @param courseId the id of a course
     * @param pageable a {@link Pageable}
     * @return Slice
     */
    Slice<CourseProgress> courseProgress(String courseId, Pageable pageable);

    /**
     * Potentially returns the {@link CourseProgress} for the specified user in the course having the
     * specified {@code courseId}.
     * @param userId the id of a user
     * @param courseId the id of a course
     * @return Optional<CourseProgress>
     */
    Optional<CourseProgress> singleUserProgressForCourse(String userId, String courseId);

    /**
     * Returns a {@link Stream} containing elements denoting the progress users having the given {@code userId}s
     * in the course having the specified {@code courseId}.
     * @param courseId the id of a course
     * @param userIds the user id's of the user's to fetch course progress for
     * @return {@link Stream}
     */
    Stream<CourseProgress> userProgressForCourse(String courseId, String... userIds);
}
