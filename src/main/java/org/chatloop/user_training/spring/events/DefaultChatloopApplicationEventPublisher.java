package org.chatloop.user_training.spring.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public class DefaultChatloopApplicationEventPublisher implements ChatloopApplicationEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public DefaultChatloopApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publishApplicationEvent(ApplicationEvent event) {
        log.debug("About to publish {}", event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }
}
