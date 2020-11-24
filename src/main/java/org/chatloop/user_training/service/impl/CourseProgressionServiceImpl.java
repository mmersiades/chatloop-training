package org.chatloop.user_training.service.impl;

import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.service.CourseProgressionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    public Stream<CourseProgress> progressForCourse(String courseId) {
        return repository.findByCourseId(courseId).stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<CourseProgress> singleUserProgressForCourse(String userId, String courseId) {
        List<CourseProgress> results = repository.findByCourseIdAndUserIds(courseId, Collections.singletonList(userId));
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Stream<CourseProgress> userProgressForCourse(String courseId, String... userIds) {
        return repository.findByCourseIdAndUserIds(courseId, Arrays.asList(userIds))
                .stream();
    }
}
