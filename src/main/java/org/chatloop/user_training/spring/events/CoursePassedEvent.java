package org.chatloop.user_training.spring.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * An {@link ApplicationEvent} which will be fired after a user has passed the last module in a course, which
 * makes him pass the course at the same time.
 */
@Getter
public class CoursePassedEvent extends CourseRelatedUserEvent {
    public CoursePassedEvent(String userId, Object source) {
        super(userId, source);
    }
}
