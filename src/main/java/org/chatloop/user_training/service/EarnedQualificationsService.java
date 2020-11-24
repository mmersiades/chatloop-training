package org.chatloop.user_training.service;

import org.chatloop.user_training.model.progress.EarnedQualifications;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * This {@code Service} wraps all logic related to qualifications which users earn while progressing through a course.
 */
public interface EarnedQualificationsService {
    /**
     * Returns the {@link EarnedQualifications} of the user having the specified {@code userId}. Will return
     * {@link Optional#EMPTY} if the user has not earned any qualification yet.
     *
     * @param userId the id of a user.
     * @return Optional<EarnedQualifications>
     */
    Optional<EarnedQualifications> fetchEarnedQualifications(String userId);

    /**
     * Returns the {@link EarnedQualifications} for all users.
     * @return Stream<EarnedQualifications>
     */
    Stream<EarnedQualifications> fetchAllEarnedQualifications();

    /**
     * Stores the given {@link EarnedQualifications}. Will overwrite any pre-existing
     * earned qualifications for that particular user.
     * @param earnedQualifications the qualifications
     */
    void storeEarnedQualifications(EarnedQualifications... earnedQualifications);

    /**
     * Removes the given {@link EarnedQualifications}.
     * @param earnedQualifications the qualifications to remove
     */
    void removeEarnedQualifications(EarnedQualifications... earnedQualifications);
}
