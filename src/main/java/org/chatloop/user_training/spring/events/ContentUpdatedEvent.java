package org.chatloop.user_training.spring.events;

import org.springframework.context.ApplicationEvent;

/**
 * An {@link ApplicationEvent} which will be fired after we have received a content update
 * from Contentful.
 */
public class ContentUpdatedEvent extends ContentRelatedChangeEvent {
    public ContentUpdatedEvent(Object source) {
        super(source);
    }
}
