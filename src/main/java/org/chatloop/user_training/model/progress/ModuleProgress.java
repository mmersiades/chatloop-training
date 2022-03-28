package org.chatloop.user_training.model.progress;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Entity
@Table(name = "module_progress")
public class ModuleProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ModuleProgressSequenceGen")
    @SequenceGenerator(name = "ModuleProgressSequenceGen", allocationSize = 0)
    private Long id;

    @Column(nullable = false)
    private String moduleId;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private CourseProgress course;

    @Column(nullable = false)
    private int incorrectAttempts = 0;

    @Column(nullable = false)
    private boolean complete = false;

    /**
     * If true marks it as complete. Otherwise increments counter.
     */
    public void onAttempt(boolean isCorrect) {
        if (!isCorrect) {
            incorrectAttempts += 1;
        }
        if (isCorrect) {
            complete = true;
        }
    }
}
