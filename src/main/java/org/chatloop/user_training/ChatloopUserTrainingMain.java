package org.chatloop.user_training;

import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.impl.StatefulCdnService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class })
@SpringBootApplication
public class ChatloopUserTrainingMain {
    public static void main(String[] args) {
        new SpringApplication(ChatloopUserTrainingMain.class).run(args);
    }

    @Bean
    public CdnService cdnService() {
        return new StatefulCdnService();
    }
}
