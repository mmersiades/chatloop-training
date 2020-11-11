package org.chatloop.user_training.service.impl;

import com.contentful.java.cda.CDAEntry;
import com.google.gson.internal.LinkedTreeMap;
import org.assertj.core.util.Lists;
import org.chatloop.user_training.IntegrationTestConfig;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.model.content.UserQualification;
import org.chatloop.user_training.service.CdnService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ActiveProfiles({"test", "qualifications"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { IntegrationTestConfig.class, StatefulCdnServiceQualificationsTest.TestConfigOverrides.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class StatefulCdnServiceQualificationsTest {

    @Profile("qualifications")
    @TestConfiguration
    static class TestConfigOverrides {
        @Bean
        @Primary
        public CdnService stubbedCdnService() {
            SimpleCDAEntry entry = new SimpleCDAEntry("qualification");
            entry.setField("name", "TopicText");
            entry.setField("requirements", Map.of("2L1kH3zjtvGkNtWLw0tJgj", 1.0, "72szZK1yf99qmh4nUVsiho", 1.0));

            return new StatefulCdnServiceStub(Map.of("E1JGCjpIANkayuNpEXQfS", entry));
        }
    }

    @Autowired
    private CdnService cdnService;

    @Test
    public void testFetchUserQualifications() {
        List<UserQualification> userQualifications = cdnService.fetchUserQualifications();
        assertEquals(1, userQualifications.size());

        UserQualification qualification = userQualifications.get(0);
        assertEquals("TopicText", qualification.getName());
        assertEquals(Set.of("2L1kH3zjtvGkNtWLw0tJgj", "72szZK1yf99qmh4nUVsiho"), qualification.getCourseIds());
    }
}
