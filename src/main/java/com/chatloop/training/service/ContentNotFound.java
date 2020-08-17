package com.chatloop.training.service;

/**
 * Thrown when program/course/module with given ID was not found in
 * Contentful's local in-JVM cache.
 */
public final class ContentNotFound extends RuntimeException {

    public ContentNotFound(String message) {
        super(message);
    }
}
