package org.chatloop.user_training.service.impl;

import org.chatloop.user_training.IntegrationTestConfig;
import org.chatloop.user_training.model.progress.EarnedQualifications;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.service.CourseProgressionService;
import org.chatloop.user_training.service.EarnedQualificationsService;
import org.chatloop.user_training.util.Fixture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { IntegrationTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class EarnedQualificationsServiceImplTest {

    @Autowired
    private EarnedQualificationsService service;

    @Test
    public void testServiceOps() {
        String johny = "johny";
        String topicTexts = "topicTexts";

        assertTrue(service.fetchEarnedQualifications(johny).isEmpty());

        EarnedQualifications singleQualification = Fixture.earnedQualifications(johny, topicTexts);
        service.storeEarnedQualifications(singleQualification);

        Optional<EarnedQualifications> earnedByJane = service.fetchEarnedQualifications(johny);
        assertTrue(earnedByJane.isPresent());
        assertEquals(singleQualification, earnedByJane.get());
    }
}