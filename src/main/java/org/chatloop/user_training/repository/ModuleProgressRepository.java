package org.chatloop.user_training.repository;

import org.chatloop.user_training.model.progress.ModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, Long> {
}

