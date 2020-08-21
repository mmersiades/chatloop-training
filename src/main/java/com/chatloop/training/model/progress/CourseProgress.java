package com.chatloop.training.model.progress;

import com.chatloop.training.service.ContentNotFound;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Seems that Contentful guarantees uniqueness of IDs within single space/env pair
 * and thus to avoid potential collisions in case single DB is used with different
 * instances of service, each configured with different space/env etc.
 */
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
@Entity
@Table(name = "course_progress")
public class CourseProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CourseProgressSequenceGen")
    @SequenceGenerator(name = "CourseProgressSequenceGen")
    private Long id;

    @Column(nullable = false)
    private String spaceId;

    @Column(nullable = false)
    private String envId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String courseId;

    @Column(name = "course_result", nullable = false)
    @Enumerated(EnumType.STRING)
    private CourseResult courseResult = CourseResult.NO_RESULT_YET;

    @OneToMany(mappedBy="course", fetch = FetchType.EAGER)
    private Set<ModuleProgress> modules = new HashSet<>();

    public void addModuleProgress(ModuleProgress moduleProgress) {
        modules.add(moduleProgress);
    }

    /**
     * Sum of incorrect attempts across all module progresses.
     */
    public int getIncorrectAttempts() {
        return modules.stream().mapToInt(ModuleProgress::getIncorrectAttempts).sum();
    }

    /**
     * We don't expect large number of modules, so linear scan is fine performance wise.
     */
    public ModuleProgress getOrCreateModuleProgress(String moduleId) {
        Optional<ModuleProgress> opt = modules.stream()
                .filter(m -> m.getModuleId().equals(moduleId)).findFirst();

        return opt.orElseGet(() -> {
            ModuleProgress progress = new ModuleProgress();
            progress.setModuleId(moduleId);
            progress.setCourse(this);
            modules.add(progress);
            return progress;
        });
    }

    public ModuleProgress getModuleProgress(String moduleId) {
        Optional<ModuleProgress> opt = modules.stream()
                .filter(m -> m.getModuleId().equals(moduleId)).findFirst();

        return opt.orElseThrow(() -> {
            throw new ContentNotFound("Module progress not found in database " + moduleId);
        });
    }

    public int getNumberOfModules() {
//        if(modules.size() == 0) { TODO FIXME
//            throw new IllegalStateException("Course progress has no modules " + getId());
//        }
        return modules.size();
    }
}
