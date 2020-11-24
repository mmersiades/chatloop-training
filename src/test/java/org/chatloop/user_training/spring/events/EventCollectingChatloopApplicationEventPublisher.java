package org.chatloop.user_training.spring.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class EventCollectingChatloopApplicationEventPublisher implements ChatloopApplicationEventPublisher {
    private final Collection<ApplicationEvent> events = new ArrayList<>();

    @Override
    public void publishApplicationEvent(ApplicationEvent event) {
        events.add(event);
    }
}
