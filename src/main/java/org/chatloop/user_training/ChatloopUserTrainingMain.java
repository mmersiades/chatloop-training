package org.chatloop.user_training;

import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.repository.EarnedQualificationsRepository;
import org.chatloop.user_training.service.*;
import org.chatloop.user_training.service.impl.*;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;
import org.chatloop.user_training.spring.events.DefaultChatloopApplicationEventPublisher;
import org.chatloop.user_training.web.ChatloopMonolithFacade;
import org.chatloop.user_training.web.impl.ChatloopMonolithFacadeImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;

//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@SpringBootApplication
public class ChatloopUserTrainingMain implements ApplicationEventPublisherAware {
    public static void main(String[] args) {
        SpringApplication.run(ChatloopUserTrainingMain.class, args);
    }

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(@NotNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Bean
    public Transformer transformer() {
        return new Transformer();
    }

    @Bean
    public ChatloopApplicationEventPublisher chatloopApplicationEventPublisher() {
        return new DefaultChatloopApplicationEventPublisher(applicationEventPublisher);
    }

    @Bean
    public CourseProgressionService courseProgressionService(@Autowired CourseProgressRepository repository) {
        return new CourseProgressionServiceImpl(repository);
    }

    @Bean
    public QualificationEvaluationService qualificationEvaluationService(@Autowired CourseProgressRepository courseProgressRepository, @Autowired EarnedQualificationsRepository earnedQualificationsRepository) {
        return new QualificationEvaluationServiceImpl(cdnService(), courseProgressionService(courseProgressRepository), earnedQualificationsService(earnedQualificationsRepository), chatloopMonolithFacade());
    }

    @Bean
    public EarnedQualificationsService earnedQualificationsService(@Autowired EarnedQualificationsRepository repository) {
        return new EarnedQualificationsServiceImpl(repository);
    }

    @Bean
    public ApplicationSecretsService applicationSecretsService() {
        return new AWSApplicationSecretsServiceImpl();
    }

    @Bean
    public ChatloopMonolithFacade chatloopMonolithFacade() {
        return new ChatloopMonolithFacadeImpl(applicationSecretsService());
    }

    @Bean
    public CdnService cdnService() {
        Transformer transformer = transformer();
        ChatloopApplicationEventPublisher chatloopApplicationEventPublisher = chatloopApplicationEventPublisher();
        return new StatefulCdnService(transformer, chatloopApplicationEventPublisher);
    }
}
