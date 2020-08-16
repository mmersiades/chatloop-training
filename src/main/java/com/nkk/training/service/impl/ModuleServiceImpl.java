package com.nkk.training.service.impl;

import com.nkk.training.model.Module;
import com.nkk.training.repository.ModuleRepository;
import com.nkk.training.service.ModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository repository;

    public ModuleServiceImpl(ModuleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Module save(Module module) {
        return this.repository.save(module);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Module> findAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Module> findOne(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    private final Logger log = LoggerFactory.getLogger(ModuleServiceImpl.class);
}
