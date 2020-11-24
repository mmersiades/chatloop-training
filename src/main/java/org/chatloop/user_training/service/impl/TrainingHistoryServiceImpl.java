package org.chatloop.user_training.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.model.event.TrainingEvent;
import org.chatloop.user_training.model.event.TrainingEventType;
import org.chatloop.user_training.repository.TrainingHistoryRepository;
import org.chatloop.user_training.service.TrainingHistoryService;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;
import org.chatloop.user_training.spring.events.CoursePassedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class TrainingHistoryServiceImpl implements TrainingHistoryService {

    @Value("${contentful.spaceId}")
    private String spaceId;
    @Value("${contentful.envId}")
    private String envId;

    @Autowired
    private final TrainingHistoryRepository repository;

    @Autowired
    private final ChatloopApplicationEventPublisher publisher;

    public TrainingHistoryServiceImpl(TrainingHistoryRepository repository, ChatloopApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public void onCourseComplete(String userId, String courseId) {
        TrainingEvent event = new TrainingEvent();
        event.setUserId(userId);
        event.setCourseId(courseId);
        event.setTrainingEventType(TrainingEventType.COURSE_COMPLETE);
        save(event);

        publisher.publishApplicationEvent(new CoursePassedEvent(userId, this));
    }

    @Override
    public void onCourseFailed(String userId, String courseId) {
        TrainingEvent event = new TrainingEvent();
        event.setUserId(userId);
        event.setCourseId(courseId);
        event.setTrainingEventType(TrainingEventType.COURSE_FAILED);
        save(event);
    }

    @Override
    public void onCourseReset(String userId, String courseId) {
        TrainingEvent event = new TrainingEvent();
        event.setUserId(userId);
        event.setCourseId(courseId);
        event.setTrainingEventType(TrainingEventType.COURSE_RESET);
        save(event);
    }

    @Override
    public void onTaskCorrect(String userId, String courseId, String moduleId) {
        TrainingEvent event = new TrainingEvent();
        event.setUserId(userId);
        event.setCourseId(courseId);
        event.setModuleId(moduleId);
        event.setTrainingEventType(TrainingEventType.TASK_CORRECT);
        save(event);
    }

    @Override
    public void onTaskIncorrect(String userId, String courseId, String moduleId) {
        TrainingEvent event = new TrainingEvent();
        event.setUserId(userId);
        event.setCourseId(courseId);
        event.setModuleId(moduleId);
        event.setTrainingEventType(TrainingEventType.TASK_INCORRECT);
        save(event);
    }

    private void save(TrainingEvent trainingEvent) {
        trainingEvent.setSpaceId(spaceId);
        trainingEvent.setEnvId(envId);
        trainingEvent = this.repository.save(trainingEvent);
        log.debug("Saved user_training event {}", trainingEvent);
    }
}
