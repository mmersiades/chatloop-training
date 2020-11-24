package org.chatloop.user_training.integration;

import org.assertj.core.util.Lists;
import org.chatloop.user_training.IntegrationTestConfig;
import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.impl.SimpleCDAEntry;
import org.chatloop.user_training.service.impl.StatefulCdnServiceStub;
import org.chatloop.user_training.service.impl.Transformer;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;
import org.chatloop.user_training.spring.events.EventCollectingChatloopApplicationEventPublisher;
import org.chatloop.user_training.util.data.Known;
import org.junit.Test;
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

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IntegrationTestConfig.class, StatefulCdnServiceTest.TestConfigOverrides.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class StatefulCdnServiceTest {

    @Autowired
    private CdnService cdnService;

    @TestConfiguration
    static class TestConfigOverrides {
        @Autowired
        private Transformer transformer;

        @Bean
        @Primary
        public CdnService stubbedCdnService() {
            SimpleCDAEntry course = new SimpleCDAEntry(Known.COURSE_ID, "course");
            course.setField("name", "TestCourse");
            course.setField("modules", Collections.emptyList());

            SimpleCDAEntry program = new SimpleCDAEntry(Known.PROGRAM_ID, "program");
            program.setField("name", "TestProgram");
            program.setField("courses", Lists.list(course));

            ChatloopApplicationEventPublisher eventPublisher = new EventCollectingChatloopApplicationEventPublisher();

            return new StatefulCdnServiceStub(
                    transformer,
                    eventPublisher,
                    Map.of(Known.PROGRAM_ID, program, Known.COURSE_ID, course)
            );
        }
    }

    @Test
    public void testBasic() {
        Optional<UserProgram> program = cdnService.fetchProgramById(Known.PROGRAM_ID);
        assertTrue(program.isPresent());
        assertEquals(Known.PROGRAM_ID, program.get().getId());

        Optional<UserCourse> course = cdnService.fetchCourseById(Known.COURSE_ID);
        assertTrue(course.isPresent());
        assertEquals(Known.COURSE_ID, course.get().getId());
    }
}
