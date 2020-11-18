package org.chatloop.user_training.spring.events;

import org.springframework.context.ApplicationEvent;

public interface ChatloopApplicationEventPublisher {
    void publishApplicationEvent(ApplicationEvent event);
}
