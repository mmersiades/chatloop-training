package com.chatloop.user_training.integration;

import com.chatloop.user_training.LocalTestConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LocalTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class CdnWebhookTest {

    @Value("${contentful.spaceId}")
    private String spaceId;
    @Value("${contentful.envId}")
    private String envId;
    @Value("${contentful.deliveryToken}")
    private String deliveryToken;

    @Test
    public void testQueryHooks() throws Exception {
        String url = String.format("https://api.contentful.com/spaces/%s/webhook_definitions", spaceId);

        HttpGet get = new HttpGet(url);
        get.addHeader("Authorization", String.format("Bearer %s", deliveryToken));
        get.addHeader("Content-Type", "application/vnd.contentful.management.v1+json");

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        // causes 403 forbidden, TODO
    }
}
