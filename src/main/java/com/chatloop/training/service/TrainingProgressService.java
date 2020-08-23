package com.chatloop.training.service;

import com.chatloop.training.model.content.UserCourse;
import com.chatloop.training.model.content.UserProgram;

import java.util.List;

/**
 * Exception is thrown if CDN does not contain resource with given ID.
 */
public interface TrainingProgressService {

    List<UserProgram> fetchUserPrograms();

    UserProgram fetchProgram(String programId);

    UserCourse fetchCourse(String courseId);

    UserCourse resetCourse(String courseId);

    UserCourse taskAttempt(String courseId, String moduleId, boolean isCorrect);
}
