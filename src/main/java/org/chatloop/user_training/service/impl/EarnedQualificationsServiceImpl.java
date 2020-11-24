package org.chatloop.user_training.service.impl;

import org.chatloop.user_training.model.progress.EarnedQualifications;
import org.chatloop.user_training.repository.EarnedQualificationsRepository;
import org.chatloop.user_training.service.EarnedQualificationsService;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@inheritDoc}
 */
public class EarnedQualificationsServiceImpl implements EarnedQualificationsService {
    private final EarnedQualificationsRepository repository;

    public EarnedQualificationsServiceImpl(EarnedQualificationsRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<EarnedQualifications> fetchEarnedQualifications(String userId) {
        return repository.findById(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<EarnedQualifications> fetchAllEarnedQualifications() {
        return repository.findAll().stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeEarnedQualifications(EarnedQualifications... earnedQualifications) {
        repository.saveAll(Arrays.asList(earnedQualifications));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEarnedQualifications(EarnedQualifications... earnedQualifications) {
        repository.deleteAll(Arrays.asList(earnedQualifications));
    }
}
