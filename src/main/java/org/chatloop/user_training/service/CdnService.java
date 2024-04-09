package org.chatloop.user_training.service;

import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.model.content.UserQualification;

import java.util.List;
import java.util.Optional;

/**
 * Acts as proxy towards remote Contentful database. Able to locally
 * cache, in-JVM, our dataset. Works under assumption that size of our
 * data will continue to be small, and thus no smarter caching policies
 * are required, for now.
 */
public interface CdnService {

    /**
     * Will be generally triggered by webhook.
     */
    void markCacheStale();

    /**
     * Fetches and returns a list of all {@link UserProgram}s from the underlying content database.
     * @return {@link List}
     */
    List<UserProgram> fetchUserPrograms();


    List<UserCourse> fetchUserCourses();

    /**
     * Fetches and returns a list of all {@link UserQualification}s from the underlying content database.
     * @return {@link List}
     */
    List<UserQualification> fetchUserQualifications();

    /**
     * If data is not in cache, should refresh the cache.
     */
    Optional<UserProgram> fetchProgramById(String programId);

    /**
     * If data is not in the cache, should refresh cache.
     */
    Optional<UserCourse> fetchCourseById(String courseId);
}
