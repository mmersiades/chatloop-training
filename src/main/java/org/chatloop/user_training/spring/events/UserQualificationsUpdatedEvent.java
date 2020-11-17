package org.chatloop.user_training.spring.events;

import org.springframework.context.ApplicationEvent;

/**
 * An {@link ApplicationEvent} which will be fired after we detect changes to the UserQualifications in
 * Contentful.
 */
public class UserQualificationsUpdatedEvent extends ContentRelatedChangeEvent {
    public UserQualificationsUpdatedEvent(Object source) {
        super(source);
    }
}
