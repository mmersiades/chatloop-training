package org.chatloop.user_training.spring.events;

import org.springframework.context.ApplicationEvent;

public abstract class ContentRelatedChangeEvent extends ApplicationEvent {
    public ContentRelatedChangeEvent(Object source) {
        super(source);
    }
}
