package org.chatloop.user_training.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.service.ApplicationSecretsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import java.io.IOException;

@Slf4j
public class AWSApplicationSecretsServiceImpl implements ApplicationSecretsService {

    @Value("${JWT_MONOLITH_REST:}")
    private String jwtMonolithRest;

    private String secretValueRaw;

    private final SecretsManagerClient client;

    public AWSApplicationSecretsServiceImpl() {
        this(Region.AP_SOUTHEAST_2);
    }

    public AWSApplicationSecretsServiceImpl(Region region) {
        client = SecretsManagerClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region)
                .build();
    }

    private void fetchSecretValueFromSecretsManager() {
        if (secretValueRaw == null) {
            String environment = System.getenv("SPRING_PROFILES_ACTIVE");
            String secretsName;
            if ("production".equals(environment)) {
                secretsName = "chatloop-training-production-microservice-secrets";
            } else if ("staging".equals(environment)) {
                secretsName = "chatloop-training-staging-microservice-secrets";
            } else {
                secretsName = "chatloop-training-local-microservice-secrets";
            }

            log.info("Looking up secrets using secret named: {}", secretsName);

            String secretValue = null;
            try {
                GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                        .secretId(secretsName)
                        .build();

                GetSecretValueResponse valueResponse = client.getSecretValue(valueRequest);
                secretValue = valueResponse.secretString();
            } catch (SecretsManagerException e) {
                log.info("Unable to fetch secret named {} from AWS SecretsManager.", secretsName, e);
            }

            this.secretValueRaw = secretValue;
        }
    }

    @Override
    public String monolithRestServiceJWT() {
        if (StringUtils.isEmpty(jwtMonolithRest)) {
            fetchSecretValueFromSecretsManager();
            if (!StringUtils.isEmpty(secretValueRaw)) {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode node = mapper.readTree(secretValueRaw);
                    jwtMonolithRest = node.get("monolith_rest_jwt").textValue();
                } catch (IOException e) {
                    log.warn("Unable to process secrets json.", e);
                    throw new IllegalStateException("Unable to extract bearer token for the Monolith.");
                }
            }
        }
        return jwtMonolithRest;
    }
}
