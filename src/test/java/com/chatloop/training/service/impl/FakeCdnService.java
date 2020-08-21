package com.chatloop.training.service.impl;

import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Program;
import com.chatloop.training.data.DataFactory;
import com.chatloop.training.service.CdnService;
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

    private final List<Program> programs = new ArrayList<>();

    @Override
    @PostConstruct
    public void init() {
        DataFactory factory = new DataFactory();
        programs.add(factory.createProgram1());
        programs.add(factory.createProgram2());
        programs.add(factory.createProgram3());
        log.info("Using fake CDN service");
    }

    @Override
    public Optional<Program> fetchProgramById(String programId) {
        return programs.stream().filter(e -> e.getId().equals(programId)).findFirst();
    }

    @Override
    public Optional<Course> fetchCourseById(String courseId) {
        for(Program program : programs) {
            Optional<Course> course = program.getCourses().stream().filter(e -> e.getId().equals(courseId)).findFirst();
            if(course.isPresent()) {
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
