package com.chatloop.user_training.repository;

import com.chatloop.user_training.model.progress.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {

    @Query("FROM CourseProgress cp WHERE cp.userId = ?1 AND cp.courseId = ?2")
    Optional<CourseProgress> findByUserIdCourseId(String userId, String courseId);

    @Query("FROM CourseProgress cp WHERE cp.spaceId = ?1 AND cp.envId = ?2 AND cp.userId = ?3 AND cp.courseId = ?4")
    Optional<CourseProgress> findByCompositeId(String spaceId, String envId,
                                               String userId, String courseId);
}

