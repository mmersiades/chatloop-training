package com.chatloop.user_training.service.impl;

import com.chatloop.user_training.model.content.UserCourse;
import com.chatloop.user_training.model.content.UserProgram;
import com.chatloop.user_training.service.CdnService;
import com.chatloop.user_training.util.data.DataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("test")
@Service
public class FakeCdnService implements CdnService {

    private final List<UserProgram> userPrograms = new ArrayList<>();

    @Override
    @PostConstruct
    public void init() {
        DataFactory factory = new DataFactory();
        userPrograms.add(factory.createProgram1());
        userPrograms.add(factory.createProgram2());
        userPrograms.add(factory.createProgram3());
        log.info("Using fake CDN service");
    }

    @Override
    public List<UserProgram> fetchUserPrograms() {
        return userPrograms;
    }

    @Override
    public Optional<UserProgram> fetchProgramById(String programId) {
        return userPrograms.stream().filter(e -> e.getId().equals(programId)).findFirst();
    }

    @Override
    public Optional<UserCourse> fetchCourseById(String courseId) {
        for (UserProgram userProgram : userPrograms) {
            Optional<UserCourse> course = userProgram.getUserCourses().stream().filter(e -> e.getId().equals(courseId)).findFirst();
            if (course.isPresent()) {
                return course;
            }
        }
        return Optional.empty();
    }

    @Override
    public void markCacheStale() {
    }

    private final Logger log = LoggerFactory.getLogger(FakeCdnService.class);
}
