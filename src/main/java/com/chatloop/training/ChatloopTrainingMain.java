package com.chatloop.training;

import com.chatloop.training.service.CdnService;
import com.chatloop.training.service.impl.StatefulCdnService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
public class ChatloopTrainingMain {
    public static void main(String[] args) {
        new SpringApplication(ChatloopTrainingMain.class).run(args);
    }

    @Bean
    public CdnService cdnService() {
        return new StatefulCdnService();
    }
}
