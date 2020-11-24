package org.chatloop.user_training.spring.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * An {@link ApplicationEvent} which will be fired after a user has reset a course.
 */
@Getter
public class CourseResetEvent extends CourseRelatedUserEvent {
    public CourseResetEvent(String userId, Object source) {
        super(userId, source);
    }
}
