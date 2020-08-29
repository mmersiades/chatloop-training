package org.chatloop.user_training.repository;

import org.chatloop.user_training.model.event.TrainingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingHistoryRepository extends JpaRepository<TrainingEvent, Long> {
}

