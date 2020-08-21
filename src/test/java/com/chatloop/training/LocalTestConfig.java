package com.chatloop.training;

import com.chatloop.training.service.CdnService;
import com.chatloop.training.service.impl.FakeCdnService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class LocalTestConfig {

    @Bean
    public CdnService cdnService() {
        return new FakeCdnService();
    }
}
