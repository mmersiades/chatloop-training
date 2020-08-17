package com.chatloop.training.model.content;

import com.chatloop.training.model.progress.ModuleProgress;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class Module extends IdAware {

    private String name;

    private boolean isComplete;

    private int incorrectAttempts;

    private Task task;

    public void refreshFrom(ModuleProgress moduleProgress) {
        isComplete = moduleProgress.isComplete();
        incorrectAttempts = moduleProgress.getIncorrectAttempts();
    }
}
