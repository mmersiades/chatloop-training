package com.chatloop.training.model.content;

import com.chatloop.training.model.progress.CourseResult;
import com.chatloop.training.service.ContentNotFound;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class UserCourse extends IdAware {

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

    private final Set<UserModule> userModules = new LinkedHashSet<>();

    public UserModule getUserModule(String moduleId) {
        Optional<UserModule> opt = userModules.stream()
                .filter(m -> m.getId().equals(moduleId)).findFirst();

        return opt.orElseThrow(() -> {
            String message = String.format("Module %s not found in course %s", moduleId, getId());
            throw new ContentNotFound(message);
        });
    }

    public void addUserModule(UserModule userModule) {
        this.userModules.add(userModule);
    }

    public boolean isEmpty() {
        return userModules.isEmpty();
    }

    public int getNumberOfUserModules() {
        return userModules.size();
    }

    /**
     * We don't care about order of modules to calculate completion of course.
     * Returns true if at most one module is left to be completed.
     */
    public boolean isLast(String moduleId) {
        return getNumberOfUserModules() - numOfCompletedModules() <= 1;
    }

    public void calculateDerivedInfo() {
        completion = numOfCompletedModules() / (float) getNumberOfUserModules();
        incorrectAttempts = userModules.stream().mapToInt(UserModule::getIncorrectAttempts).sum();
    }

    private int numOfCompletedModules() {
        return (int) userModules.stream().filter(UserModule::isComplete).count();
    }
}
