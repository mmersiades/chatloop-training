package com.chatloop.training.web;

import com.chatloop.training.model.content.UserCourse;
import com.chatloop.training.model.content.UserProgram;
import com.chatloop.training.service.TrainingProgressService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingProgressQueryResolver implements GraphQLQueryResolver {

    @Autowired
    private final TrainingProgressService trainingProgressService;

    public TrainingProgressQueryResolver(TrainingProgressService trainingProgressService) {
        this.trainingProgressService = trainingProgressService;
    }

    public List<UserProgram> userPrograms() {
        return trainingProgressService.fetchUserPrograms();
    }

    public UserProgram userProgram(String programId) {
        return trainingProgressService.fetchProgram(programId);
    }

    public UserCourse userCourse(String courseId) {
        return trainingProgressService.fetchCourse(courseId);
    }
}
