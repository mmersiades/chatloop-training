package org.chatloop.user_training;

import org.chatloop.user_training.service.ApplicationSecretsService;
import org.chatloop.user_training.web.ChatloopMonolithFacade;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class IntegrationTestConfig {

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
