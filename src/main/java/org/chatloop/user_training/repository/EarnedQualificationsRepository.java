package org.chatloop.user_training.repository;

import org.chatloop.user_training.model.progress.EarnedQualifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EarnedQualificationsRepository extends JpaRepository<EarnedQualifications, String> {
}
