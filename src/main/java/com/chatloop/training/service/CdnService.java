package com.chatloop.training.service;

import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Program;

import java.util.Optional;

/**
 * Acts as proxy towards remote Contentful database. Able to locally
 * cache, in-JVM, our dataset. Works under assumption that size of our
 * data will continue to be small, and thus no smarter caching policies
 * are required, for now.
 */
public interface CdnService {

    /**
     * Will perform initial cache sync.
     */
    void init();

    /**
     * Will be generally triggered by webhook.
     */
    void markCacheStale();

    /**
     * If data is not in cache, should refresh the cache.
     */
    Optional<Program> fetchProgramById(String programId);

    /**
     * If data is not in the cache, should refresh cache.
     */
    Optional<Course> fetchCourseById(String courseId);
}
