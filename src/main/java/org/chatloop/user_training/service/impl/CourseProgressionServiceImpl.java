package org.chatloop.user_training.service.impl;

import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.service.CourseProgressionService;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@inheritDoc}
 */
public class CourseProgressionServiceImpl implements CourseProgressionService {
    private final CourseProgressRepository repository;

    public CourseProgressionServiceImpl(CourseProgressRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<CourseProgress> userProgressForCourse(String courseId) {
        return repository.findByCourseId(courseId).stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CourseProgress> userProgressForCourse(String userId, String courseId) {
        return repository.findByUserIdCourseId(userId, courseId);
    }
}
