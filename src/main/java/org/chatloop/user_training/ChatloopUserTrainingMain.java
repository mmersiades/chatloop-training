package org.chatloop.user_training;

import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.CourseProgressionService;
import org.chatloop.user_training.service.impl.CourseProgressionServiceImpl;
import org.chatloop.user_training.service.impl.StatefulCdnService;
import org.chatloop.user_training.service.impl.Transformer;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;
import org.chatloop.user_training.spring.events.DefaultChatloopApplicationEventPublisher;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;

//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@SpringBootApplication
public class ChatloopUserTrainingMain implements ApplicationEventPublisherAware {
    public static void main(String[] args) {
        new SpringApplication(ChatloopUserTrainingMain.class).run(args);
    }

    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
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
    public CourseProgressionService courseProgressionService(@Autowired CourseProgressRepository courseProgressRepository) {
        return new CourseProgressionServiceImpl(courseProgressRepository);
    }

    @Bean
    public CdnService cdnService() {
        Transformer transformer = transformer();
        ChatloopApplicationEventPublisher chatloopApplicationEventPublisher = chatloopApplicationEventPublisher();
        return new StatefulCdnService(transformer, chatloopApplicationEventPublisher);
    }
}
