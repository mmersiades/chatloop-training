package org.chatloop.user_training.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.service.ApplicationSecretsService;
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

    private final String secretValueRaw;

    public AWSApplicationSecretsServiceImpl() {
        this(Region.AP_SOUTHEAST_2);
    }

    public AWSApplicationSecretsServiceImpl(Region region) {
        this(
            SecretsManagerClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region)
                .build()
        );
    }

    public AWSApplicationSecretsServiceImpl(SecretsManagerClient client) {
        String environment = System.getenv("SPRING_PROFILES_ACTIVE");
        String secretsName = ("production".equals(environment)) ? "chatloop-training-production-microservice-secrets" : "chatloop-training-staging-microservice-secrets";
        log.debug("Looking up secrets using secret named: {}", secretsName);

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

    @Override
    public String monolithRestServiceJWT() {
        if (StringUtils.isEmpty(secretValueRaw)) {
            return "";
        } else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(secretValueRaw);
                return node.get("monolith_rest_jwt").textValue();
            } catch (IOException e) {
                log.warn("Unable to process secrets json.", e);
                throw new IllegalStateException("Unable to extract bearer token for the Monolith");
            }
        }
    }
}
