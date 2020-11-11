package org.chatloop.user_training;

import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.impl.StatefulCdnService;
import org.chatloop.user_training.service.impl.Transformer;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class IntegrationTestConfig {

    private final Transformer transformer;
    private final ChatloopApplicationEventPublisher publisher;

    public IntegrationTestConfig(Transformer transformer, ChatloopApplicationEventPublisher publisher) {
        this.transformer = transformer;
        this.publisher = publisher;
    }

    @Bean
    public CdnService cdnService() {
        return new StatefulCdnService(transformer, publisher);
    }
}
