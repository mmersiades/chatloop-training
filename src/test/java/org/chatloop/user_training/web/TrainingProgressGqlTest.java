package org.chatloop.user_training.web;

import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.service.TrainingProgressService;
import org.chatloop.user_training.util.TestUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LocalTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
@Slf4j
public class TrainingProgressGqlTest {

    @Autowired
    private GraphQLTestTemplate graphQLTestTemplate;
    @Autowired
    private TrainingProgressService service;
    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        graphQLTestTemplate.addHeader("Authorization", "Bearer " + TestUtil.TEST_JWT);
        log.info("Injected test JWT token for user {}", TestUtil.TEST_USER);
    }

    @Test
    @Order(1)
    public void testFetchUserPrograms() throws Exception {
        String templateFile = "graphql/userPrograms.graphql";

        ObjectNode variables = newVars();

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode programs = root.path("data").path("userPrograms");

        for (int i = 0; i < 3; i++) {
            assertTrue(programs.has(i));
            assertEquals("pro" + (i + 1), programs.get(i).path("id").textValue());
        }
    }

    /**
     * See example-fetchProgram.json for response JSON structure.
     */
    @Test
    @Order(2)
    public void testFetchUserProgram() throws Exception {
        String templateFile = "graphql/userProgram.graphql";

        ObjectNode variables = newVars();
        variables.put("programId", "pro1");

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("userProgram");
        String progId = fetchProgram.path("id").textValue();

        assertEquals("pro1", progId);
    }

    @Test
    @Order(3)
    public void testFetchUserCourse() throws Exception {
        String templateFile = "graphql/userCourse.graphql";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("courseId", "cou1");

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("userCourse");
        String courId = fetchProgram.path("id").textValue();

        assertEquals("cou1", courId);
    }

    @Test
    @Order(4)
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
    @Order(5)
    public void testResetCourse() throws Exception {
        String templateFile = "graphql/resetUserCourse.graphql";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("courseId", "cou1");

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("resetUserCourse");
        String courId = fetchProgram.path("id").textValue();

        assertEquals("cou1", courId);
    }

    private ObjectNode newVars() {
        return objectMapper.createObjectNode();
    }
}
