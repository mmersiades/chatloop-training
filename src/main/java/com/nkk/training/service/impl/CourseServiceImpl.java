package com.nkk.training.service.impl;
import com.nkk.training.model.Course;
import com.nkk.training.repository.CourseRepository;
import com.nkk.training.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;

    public CourseServiceImpl(CourseRepository repository) {
        this.repository = repository;
    }

    @Override
    public Course save(Course program) {
        return this.repository.save(program);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Course> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Course> findOne(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    private final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);
}