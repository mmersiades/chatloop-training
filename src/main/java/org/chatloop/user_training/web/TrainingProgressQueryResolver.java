package org.chatloop.user_training.web;

import graphql.kickstart.tools.GraphQLQueryResolver;
import org.chatloop.user_training.model.content.EarnedQualification;
import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.model.progress.EarnedQualifications;
import org.chatloop.user_training.service.EarnedQualificationsService;
import org.chatloop.user_training.service.TrainingProgressService;
import org.chatloop.user_training.spring.security.SecurityContextUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainingProgressQueryResolver implements GraphQLQueryResolver {

    private final TrainingProgressService trainingProgressService;
    private final EarnedQualificationsService earnedQualificationsService;

    public TrainingProgressQueryResolver(TrainingProgressService trainingProgressService, EarnedQualificationsService earnedQualificationsService) {
        this.trainingProgressService = trainingProgressService;
        this.earnedQualificationsService = earnedQualificationsService;
    }

    public List<UserProgram> userPrograms() {
        return trainingProgressService.fetchUserPrograms();
    }

    public List<EarnedQualification> earnedQualifications() {
        String userId = SecurityContextUtils.getUserId();
        return earnedQualificationsService
                .fetchEarnedQualifications(userId)
                .map(EarnedQualifications::getQualifications)
                .orElse(Collections.emptySet())
                .stream()
                .map(qualificationName -> {
                    EarnedQualification earnedQualification = new EarnedQualification();
                    earnedQualification.setId(String.format("%s::%s", userId, qualificationName));
                    earnedQualification.setName(qualificationName);
                    return earnedQualification;
                }).collect(Collectors.toList());
    };

    public UserProgram userProgram(String programId) {
        return trainingProgressService.fetchProgram(programId);
    }

    public UserCourse userCourse(String courseId) {
        return trainingProgressService.fetchCourse(courseId);
    }
}
