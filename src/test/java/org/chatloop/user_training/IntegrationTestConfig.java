package org.chatloop.user_training;

import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.impl.StatefulCdnService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    public CdnService cdnService() {
        return new StatefulCdnService();
    }
}
