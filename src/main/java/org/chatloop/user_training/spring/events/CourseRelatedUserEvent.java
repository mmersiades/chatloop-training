package org.chatloop.user_training.spring.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public abstract class CourseRelatedUserEvent extends ApplicationEvent {
    private final String userId;

    public CourseRelatedUserEvent(String userId, Object source) {
        super(source);
        this.userId = userId;
    }
}
