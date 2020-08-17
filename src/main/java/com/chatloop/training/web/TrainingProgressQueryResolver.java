package com.chatloop.training.web;

import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Program;
import com.chatloop.training.service.TrainingProgressService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingProgressQueryResolver implements GraphQLQueryResolver {

    @Autowired
    private final TrainingProgressService trainingProgressService;

    public TrainingProgressQueryResolver(TrainingProgressService trainingProgressService) {
        this.trainingProgressService = trainingProgressService;
    }

    public Program fetchProgram(String programId) {
        return trainingProgressService.fetchProgram(programId);
    }

    public Course fetchCourse(String courseId) {
        return trainingProgressService.fetchCourse(courseId);
    }
}
