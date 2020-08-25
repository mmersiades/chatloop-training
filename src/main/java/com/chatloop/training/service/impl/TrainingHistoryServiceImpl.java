package com.chatloop.training.service.impl;

import com.chatloop.training.model.event.TrainingEvent;
import com.chatloop.training.model.event.TrainingEventType;
import com.chatloop.training.repository.TrainingHistoryRepository;
import com.chatloop.training.service.TrainingHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public TrainingHistoryServiceImpl(TrainingHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onCourseComplete(String userId, String courseId) {
        TrainingEvent event = new TrainingEvent();
        event.setUserId(userId);
        event.setCourseId(courseId);
        event.setTrainingEventType(TrainingEventType.COURSE_COMPLETE);
        save(event);
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
        log.debug("Saved training event {}", trainingEvent);
    }
}
