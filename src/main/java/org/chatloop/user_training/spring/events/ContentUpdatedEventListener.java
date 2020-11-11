package org.chatloop.user_training.spring.events;

import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.service.CdnService;
import org.springframework.context.ApplicationListener;

@Slf4j
public class ContentUpdatedEventListener implements ApplicationListener<ContentUpdatedEvent> {

    @Override
    public void onApplicationEvent(ContentUpdatedEvent contentUpdatedEvent) {
        log.debug("Received {}", contentUpdatedEvent.getClass().getSimpleName());
    }
}
