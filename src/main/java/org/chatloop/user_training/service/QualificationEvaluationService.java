package org.chatloop.user_training.service;

import org.chatloop.user_training.model.progress.EvaluationResult;

/**
 * This service calculates if users qualify for qualifications based on their course progress.
 */
public interface QualificationEvaluationService {

    /**
     * Evaluates the course progress for all users and computes their earned qualifications.
     * @return EvaluationResult
     */
    EvaluationResult evaluateAllUsers();

    /**
     * Evaluates the course progress for the given users and computes their earned qualifications.
     * @return EvaluationResult
     */
    EvaluationResult evaluateUsers(String... userIds);
}
