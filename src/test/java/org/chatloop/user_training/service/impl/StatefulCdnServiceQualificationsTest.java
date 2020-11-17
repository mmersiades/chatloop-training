package org.chatloop.user_training.service.impl;

import org.chatloop.user_training.IntegrationTestConfig;
import org.chatloop.user_training.model.content.UserQualification;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;
import org.chatloop.user_training.spring.events.UserQualificationsUpdatedEvent;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles({"test", "qualifications"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { IntegrationTestConfig.class, StatefulCdnServiceQualificationsTest.TestConfigOverrides.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class StatefulCdnServiceQualificationsTest {

    @Autowired
    private ChatloopApplicationEventPublisher publisher;

    @Profile("qualifications")
    @TestConfiguration
    static class TestConfigOverrides {
        @Autowired
        private Transformer transformer;

        @Bean
        @Primary
        public CdnService stubbedCdnService() {
            SimpleCDAEntry entry1 = new SimpleCDAEntry("qualification");
            entry1.setField("name", "TopicText");
            entry1.setField("requirements", Map.of("2L1kH3zjtvGkNtWLw0tJgj", 1.0, "72szZK1yf99qmh4nUVsiho", 1.0));

            SimpleCDAEntry entry2 = new SimpleCDAEntry("qualification");
            entry2.setField("name", "TwentyQuestions");
            entry2.setField("requirements", Map.of("981kH3zjtvGkNtWLw0tJgj", 1.0));

            ChatloopApplicationEventPublisher eventPublisher = publisher();

            return new StatefulCdnServiceStub(
                    transformer,
                    eventPublisher,
                    Map.of("E1JGCjpIANkayuNpEXQfS", entry1),
                    Map.of("F2HGCjpIANkayuNpEXQfS", entry2)
            );
        }

        @Bean
        @Primary
        public ChatloopApplicationEventPublisher publisher() {
            return new EventCollectingChatloopApplicationEventPublisher();
        }
    }

    @Autowired
    private CdnService cdnService;

    @DisplayName("should fetch user qualifications")
    @Test
    public void testFetchUserQualifications() {
        List<UserQualification> userQualifications = cdnService.fetchUserQualifications();
        assertEquals(1, userQualifications.size());

        UserQualification qualification = userQualifications.get(0);
        if ("TopicText".equals(qualification.getName())) {
            assertEquals(Set.of("2L1kH3zjtvGkNtWLw0tJgj", "72szZK1yf99qmh4nUVsiho"), qualification.getCourseIds());
        } else if ("TwentyQuestions".equals(qualification.getName())) {
            assertEquals(Set.of("981kH3zjtvGkNtWLw0tJgj"), qualification.getCourseIds());
        } else {
            fail(String.format("Unexpected qualification: %s", qualification.getName()));
        }
    }

    @DisplayName("should publish UserQualificationsUpdatedEvent if user qualifications are changing")
    @Test
    public void testMarkingCacheStaleMayFireEvent() {
        List<UserQualification> preUserQualifications = cdnService.fetchUserQualifications();
        assertEquals(1, preUserQualifications.size());
        assertEquals("TopicText", preUserQualifications.stream().findFirst().map(UserQualification::getName).orElse(""));

        // this should trigger an update to the user qualifications
        cdnService.markCacheStale();

        List<UserQualification> postUserQualifications = cdnService.fetchUserQualifications();
        assertEquals(1, postUserQualifications.size());
        assertEquals("TwentyQuestions", postUserQualifications.stream().findFirst().map(UserQualification::getName).orElse(""));

        assertTrue(publisher instanceof EventCollectingChatloopApplicationEventPublisher);
        var chatloopApplicationEventPublisher = (EventCollectingChatloopApplicationEventPublisher) this.publisher;
        var publishedEvents = new ArrayList<>(chatloopApplicationEventPublisher.events);
        assertEquals(1, publishedEvents.stream().filter(item -> item.getClass().isAssignableFrom(UserQualificationsUpdatedEvent.class)).count());
    }

    private static class EventCollectingChatloopApplicationEventPublisher implements ChatloopApplicationEventPublisher {
        private final Collection<ApplicationEvent> events = new ArrayList<>();

        @Override
        public void publishEvent(ApplicationEvent event) {
            events.add(event);
        }
    }
}
