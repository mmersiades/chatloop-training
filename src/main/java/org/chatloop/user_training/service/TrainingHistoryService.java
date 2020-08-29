package org.chatloop.user_training.service;

/**
 * Stores to database all key user_training related events that take place.
 * Write once semantics, once written, records should not be changed.
 */
public interface TrainingHistoryService {

    void onCourseComplete(String userId, String courseId);

    void onCourseFailed(String userId, String courseId);

    void onCourseReset(String userId, String courseId);

    void onTaskCorrect(String userId, String courseId, String moduleId);

    void onTaskIncorrect(String userId, String courseId, String moduleId);
}
