package com.nkk.training.service;

import com.nkk.training.model.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ModuleService {

    Module save(Module module);

    Page<Module> findAll(Pageable pageable);

    Optional<Module> findOne(Long id);

    void delete(Long id);
}