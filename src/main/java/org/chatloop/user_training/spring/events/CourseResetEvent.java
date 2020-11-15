package org.chatloop.user_training.spring.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * An {@link ApplicationEvent} which will be fired after a user has reset a course.
 */
@Getter
public class CourseResetEvent extends ApplicationEvent {
    private final String userId;

    public CourseResetEvent(String userId, Object source) {
        super(source);
        this.userId = userId;
    }
}
