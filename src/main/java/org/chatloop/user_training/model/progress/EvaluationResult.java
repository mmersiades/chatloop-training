package org.chatloop.user_training.model.progress;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Encapsulates the results when evaluating user qualifications based on their course progress.
 */
@Slf4j
public final class EvaluationResult {
    private final ConcurrentMap<String, Set<String>> qualifiedUsers = new ConcurrentHashMap<>();

    public void addQualifyingUser(String qualificationName, String userId) {
        qualifiedUsers.putIfAbsent(qualificationName, new HashSet<>());
        qualifiedUsers.computeIfPresent(qualificationName, (element, currentElements) -> {
            currentElements.add(userId);
            return currentElements;
        });
    }

    /**
     * Converts this {@link EvaluationResult} into a {@link Stream} of {@link EarnedQualifications}.
     * @return Stream
     */
    public Stream<EarnedQualifications> asEarnedQualifications() {
        return qualifiedUsers
                .values()
                .stream()
                .flatMap(Collection::stream)
                .distinct()
                .map(userId -> {
                    var userEarnedQualifications = qualifiedUsers
                            .keySet()
                            .stream()
                            .filter(qualificationName -> qualifiedUsers.get(qualificationName).contains(userId))
                            .collect(Collectors.toSet());
                    EarnedQualifications earnedQualifications = new EarnedQualifications();
                    earnedQualifications.setUserId(userId);
                    earnedQualifications.setQualifications(userEarnedQualifications);
                    return earnedQualifications;
                });
    }

    /**
     * Merges this {@link EvaluationResult} with the given other {@link EvaluationResult}.
     * @param other another {@link EvaluationResult}
     * @return merged {@link EvaluationResult}
     */
    public EvaluationResult merge(EvaluationResult other) {
        var otherQualifications = other.qualifiedUsers;
        for (String qualificationName : otherQualifications.keySet()) {
            log.debug("Merging qualification {}", qualificationName);
            var otherEarners = otherQualifications.get(qualificationName);

            if (qualifiedUsers.containsKey(qualificationName)) {
                var thisEarners = qualifiedUsers.get(qualificationName);
                thisEarners.addAll(otherEarners);
                qualifiedUsers.put(qualificationName, thisEarners);
            } else {
                qualifiedUsers.put(qualificationName, otherEarners);
            }
        }
        return this;
    }
}
