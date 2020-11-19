package org.chatloop.user_training.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.graphql.spring.boot.test.GraphQLTestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.model.progress.EarnedQualifications;
import org.chatloop.user_training.service.EarnedQualificationsService;
import org.chatloop.user_training.service.TrainingProgressService;
import org.chatloop.user_training.util.TestUtil;
import org.chatloop.user_training.util.enums.Courses;
import org.chatloop.user_training.util.enums.Qualifications;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Stream;

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
    @Autowired
    private EarnedQualificationsService earnedQualificationsService;

    @TestConfiguration
    static class TestConfigOverrides {
        @Bean
        @Primary
        public EarnedQualificationsService earnedQualificationsService() {
            return new EarnedQualificationsService() {
                private final List<EarnedQualifications> elements = new ArrayList<>();

                @Override
                public Optional<EarnedQualifications> fetchEarnedQualifications(String userId) {
                    return elements.stream()
                            .filter(eqf -> userId.equals(eqf.getUserId()))
                            .findFirst();
                }

                @Override
                public Stream<EarnedQualifications> fetchAllEarnedQualifications() {
                    return elements.stream();
                }

                @Override
                public void storeEarnedQualifications(EarnedQualifications... earnedQualifications) {
                    elements.addAll(Arrays.asList(earnedQualifications));
                }

                @Override
                public void removeEarnedQualifications(EarnedQualifications... earnedQualifications) {
                    throw new UnsupportedOperationException("not implemented");
                }
            };
        }
    }

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
        variables.put("courseId", Courses.COU1.name());

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("userCourse");
        String courId = fetchProgram.path("id").textValue();

        assertEquals(Courses.COU1.name(), courId);
    }

    @Test
    @Order(4)
    public void testTaskAttempt() throws Exception {
        String templateFile = "graphql/taskAttempt.graphql";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("courseId", Courses.COU1.name());
        variables.put("moduleId", "mod1");
        variables.put("isCorrect", true);

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("taskAttempt");
        String courId = fetchProgram.path("id").textValue();

        assertEquals(Courses.COU1.name(), courId);
    }

    @Test
    @Order(5)
    public void testResetCourse() throws Exception {
        String templateFile = "graphql/resetUserCourse.graphql";

        ObjectNode variables = objectMapper.createObjectNode();
        variables.put("courseId", Courses.COU1.name());

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode fetchProgram = root.path("data").path("resetUserCourse");
        String courId = fetchProgram.path("id").textValue();

        assertEquals(Courses.COU1.name(), courId);
    }

    @Test
    @Order(6)
    public void testFetchEarnedQualifications() throws Exception {
        var earnedQualifications = new EarnedQualifications();
        earnedQualifications.setUserId(TestUtil.TEST_JWT_USER_ID);
        earnedQualifications.setQualifications(Collections.singleton(Qualifications.TOPIC_TEXTS.name()));
        earnedQualificationsService.storeEarnedQualifications(earnedQualifications);

        String templateFile = "graphql/earnedQualifications.graphql";

        ObjectNode variables = objectMapper.createObjectNode();

        GraphQLResponse response = graphQLTestTemplate.perform(templateFile, variables);

        assertTrue(response.isOk());

        JsonNode root = response.readTree();
        JsonNode qualifications = root.path("data").path("earnedQualifications");
        assertEquals(1, qualifications.size());
        assertEquals(Qualifications.TOPIC_TEXTS.name(), qualifications.get(0).path("name").textValue());
    }

    private ObjectNode newVars() {
        return objectMapper.createObjectNode();
    }
}
