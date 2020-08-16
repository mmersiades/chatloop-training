package com.nkk.training.service;

import com.nkk.training.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CourseService {

    Course save(Course course);

    Page<Course> findAll(Pageable pageable);

    Optional<Course> findOne(Long id);

    void delete(Long id);
}