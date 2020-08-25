package com.chatloop.user_training.web;

import com.chatloop.user_training.model.content.UserCourse;
import com.chatloop.user_training.service.TrainingProgressService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingProgressMutateResolver implements GraphQLMutationResolver {

    @Autowired
    private final TrainingProgressService trainingProgressService;

    public TrainingProgressMutateResolver(TrainingProgressService trainingProgressService) {
        this.trainingProgressService = trainingProgressService;
    }

    public UserCourse resetUserCourse(String courseId) {
        return this.trainingProgressService.resetCourse(courseId);
    }

    public UserCourse taskAttempt(String courseId, String moduleId, boolean isCorrect) {
        return this.trainingProgressService.taskAttempt(courseId, moduleId, isCorrect);
    }
}
