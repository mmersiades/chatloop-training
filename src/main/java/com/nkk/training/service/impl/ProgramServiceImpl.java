package com.nkk.training.service.impl;

import com.nkk.training.model.Program;
import com.nkk.training.repository.ProgramRepository;
import com.nkk.training.service.ProgramService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository repository;

    public ProgramServiceImpl(ProgramRepository repository) {
        this.repository = repository;
    }

    @Override
    public Program save(Program program) {
        return this.repository.save(program);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Program> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Program> findOne(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    private final Logger log = LoggerFactory.getLogger(ProgramServiceImpl.class);
}
