package org.chatloop.user_training.model.content;

import org.chatloop.user_training.model.progress.ModuleProgress;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class UserModule extends IdAware {

    private String name;

    private boolean isComplete;

    private int incorrectAttempts;

    public void refreshFrom(ModuleProgress moduleProgress) {
        isComplete = moduleProgress.isComplete();
        incorrectAttempts = moduleProgress.getIncorrectAttempts();
    }
}
