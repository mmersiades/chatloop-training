package org.chatloop.user_training;

import org.chatloop.user_training.service.ApplicationSecretsService;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.impl.FakeCdnService;
import org.chatloop.user_training.web.ChatloopMonolithFacade;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class LocalTestConfig {

    @Bean
    public CdnService cdnService() {
        return new FakeCdnService();
    }

    @Bean
    public ChatloopMonolithFacade chatloopMonolithFacade() {
        return summary -> {
            // do nothing
        };
    }

    @Bean
    public ApplicationSecretsService applicationSecretsService() {
        return () -> "";
    }
}
