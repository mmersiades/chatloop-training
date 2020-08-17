package com.chatloop.training.web;

import com.chatloop.training.CleanDbBeforeTests;
import com.chatloop.training.LocalTestConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import com.chatloop.training.service.TrainingProgressService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LocalTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CleanDbBeforeTests
@Ignore
public class TrainingProgressGqlTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;
    @Autowired
    private TrainingProgressService service;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * See example-fetchProgram.json for response JSON structure.
     */
    @Test
    @Order(1)
    public void testFetchProgram() throws Exception {
        String templateFile = "graphql/fetchProgram.graphql";

        ObjectNode variables = newVars();
        variables.put("programId", "pro1");

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("fetchProgram");
        String progId = fetchProgram.path("id").textValue();

        assertEquals("pro1", progId);
    }

    @Test
    @Order(2)
    public void testFetchCourse() throws Exception {
        String templateFile = "graphql/fetchCourse.graphql";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("courseId","cou1");

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("fetchCourse");
        String courId = fetchProgram.path("id").textValue();

        assertEquals("cou1", courId);
    }

    @Test
    @Order(3)
    public void testTaskAttempt() throws Exception {
        String templateFile = "graphql/taskAttempt.graphql";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("courseId", "cou1");
        variables.put("moduleId", "mod1");
        variables.put("isCorrect", true);

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("taskAttempt");
        String courId = fetchProgram.path("id").textValue();

        assertEquals("cou1", courId);
    }

    @Test
    @Order(4)
    public void testResetCourse() throws Exception {
        String templateFile = "graphql/resetCourse.graphql";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("courseId", "cou1");

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("resetCourse");
        String courId = fetchProgram.path("id").textValue();

        assertEquals("cou1", courId);
    }

    private ObjectNode newVars() {
        return objectMapper.createObjectNode();
    }
}
