package org.chatloop.user_training.service;

import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserProgram;

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
