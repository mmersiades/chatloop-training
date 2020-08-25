package com.chatloop.training.model.event;

import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Generic event capable to capture all key transitions/changes
 * in the system. Generally immutable once created.
 */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Entity
@Table(name = "training_event")
@ToString
public class TrainingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TrainingEventSequenceGen")
    @SequenceGenerator(name = "TrainingEventSequenceGen")
    private Long id;

    @Column(nullable = false)
    private String spaceId;

    @Column(nullable = false)
    private String envId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = true)
    private String moduleId; // null for course-level events

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TrainingEventType trainingEventType;
}
