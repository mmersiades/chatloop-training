package com.chatloop.training.service;

import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Program;

/**
 * Exception is thrown if CDN does not contain resource with given ID.
 */
public interface TrainingProgressService {

    Program fetchProgram(String programId);

    Course fetchCourse(String courseId);

    Course resetCourse(String courseId);

    Course taskAttempt(String courseId, String moduleId, boolean isCorrect);
}
