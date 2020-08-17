package com.chatloop.training.model.content;

import com.chatloop.training.model.progress.CourseResult;
import com.chatloop.training.service.ContentNotFound;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class Course extends IdAware {

    @Setter
    private String name;

    @Setter
    private String description;

    @Setter
    private float completion;

    @Setter
    private int incorrectAttempts;

    @Setter
    private CourseResult courseResult = CourseResult.NO_RESULT_YET;

    private final Set<Module> modules = new LinkedHashSet<>();

    public Module getModule(String moduleId) {
        Optional<Module> opt = modules.stream()
                .filter(m -> m.getId().equals(moduleId)).findFirst();

        return opt.orElseThrow(() -> {
            throw new ContentNotFound("Module not found in CDN cache " + moduleId);
        });
    }

    public void addModule(Module module) {
        this.modules.add(module);
    }

    public int getNumberOfModules() {
        if(modules.size() == 0) {
            throw new IllegalStateException("Course has no modules " + getId());
        }
        return modules.size();
    }

    /**
     * Relies on fact that LinkedHashSet is order preserving and that order
     * in which Contentful returns modules is order in which application expects
     * it. TODO check and relax/improve these assumptions
     */
    public Module last() {
        Module lastModule = new ArrayList<>(modules).get(getNumberOfModules() - 1);
        return lastModule;
    }

    public boolean isLast(String moduleId) {
        return last().getId().equals(moduleId);
    }

    public void calculateDerivedInfo() {
        completion = numOfCompletedModules() / (float) getNumberOfModules();
        incorrectAttempts = modules.stream().mapToInt(Module::getIncorrectAttempts).sum();
    }

    private int numOfCompletedModules() {
        return (int) modules.stream().filter(Module::isComplete).count();
    }
}
