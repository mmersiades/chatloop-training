package com.nkk.training.service.impl;

import com.nkk.training.model.Task;
import com.nkk.training.repository.TaskRepository;
import com.nkk.training.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Task save(Task task) {
        return this.repository.save(task);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Task> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Task> findOne(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
}
