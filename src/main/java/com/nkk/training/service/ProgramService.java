package com.nkk.training.service;

import com.nkk.training.model.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProgramService {

    Program save(Program program);

    Page<Program> findAll(Pageable pageable);

    Optional<Program> findOne(Long id);

    void delete(Long id);
}
