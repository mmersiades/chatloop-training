package com.chatloop.training;

import com.chatloop.training.service.CdnService;
import com.chatloop.training.service.impl.StatefulCdnService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class IntegrationTestConfig {

    @Bean
    public CdnService cdnService() {
        return new StatefulCdnService();
    }
}
