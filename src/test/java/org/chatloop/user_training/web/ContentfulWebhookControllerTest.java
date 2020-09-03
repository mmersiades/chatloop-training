package org.chatloop.user_training.web;

import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.service.impl.FakeCdnService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LocalTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
@Slf4j
public class ContentfulWebhookControllerTest {

    @Value("${server.port}")
    private int port;
    @Autowired
    private FakeCdnService cdnService;
    @Test
    public void testWebhookReset() {
        final Instant before = cdnService.getLastReset();

        String path = "http://localhost:" + port + "/webhook";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(ContentfulWebhookController.Headers.HOOK_NAME, "TestHook");
        headers.add(ContentfulWebhookController.Headers.TOPIC_NAME, "TestTopic");

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        restTemplate.exchange(path, HttpMethod.POST, entity, String.class);

        final Instant after = cdnService.getLastReset();

        assertTrue(before.isBefore(after));
    }
}
