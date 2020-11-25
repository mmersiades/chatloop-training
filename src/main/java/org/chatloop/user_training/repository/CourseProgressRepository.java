package org.chatloop.user_training.repository;

import org.chatloop.user_training.model.progress.CourseProgress;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {

    @Query("FROM CourseProgress cp WHERE cp.courseId = ?1 AND cp.userId in ?2")
    List<CourseProgress> findByCourseIdAndUserIds(String courseId, List<String> userIds);

    @Query("FROM CourseProgress cp WHERE cp.courseId = ?1")
    List<CourseProgress> findByCourseId(String courseId);

    @Query("FROM CourseProgress cp WHERE cp.courseId = ?1")
    Slice<CourseProgress> sliceByCourseId(String courseId, Pageable pageable);

    @Query("FROM CourseProgress cp WHERE cp.spaceId = ?1 AND cp.envId = ?2 AND cp.userId = ?3 AND cp.courseId = ?4")
    Optional<CourseProgress> findByCompositeId(String spaceId, String envId,
                                               String userId, String courseId);
}

