package com.chatloop.training.web;

import com.chatloop.training.model.content.Course;
import com.chatloop.training.service.TrainingProgressService;
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

    public Course resetCourse(String courseId) {
        return this.trainingProgressService.resetCourse(courseId);
    }

    public Course taskAttempt(String courseId, String moduleId, boolean isCorrect) {
        return this.trainingProgressService.taskAttempt(courseId, moduleId, isCorrect);
    }
}
