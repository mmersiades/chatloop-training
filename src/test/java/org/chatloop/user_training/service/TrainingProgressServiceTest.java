package org.chatloop.user_training.service;

import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserModule;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.model.progress.CourseResult;
import org.chatloop.user_training.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
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
public class TrainingProgressServiceTest {

    @Autowired
    private TrainingProgressService service;

    @Before
    public void setUp() {
        TestUtil.injectAuthentication();
    }

    @Test
    public void testBasicQuery() {
        UserProgram userProgram = service.fetchProgram("pro1");
        UserCourse userCourse = service.fetchCourse("cou1");
        UserModule userModule = userCourse.getUserModule("mod1");

        assertEquals("pro1", userProgram.getId());
        assertEquals("cou1", userCourse.getId());
        assertEquals("mod1", userModule.getId());
    }

    @Test
    public void testConversationProg1() {
        UserProgram userProgram = service.fetchProgram("pro1");
        UserCourse userCourse = service.fetchCourse("cou1");
        UserModule userModule = userCourse.getUserModule("mod1");

        userCourse = service.taskAttempt(userCourse.getId(), userModule.getId(), true);
        userModule = userCourse.getUserModule("mod1");

        assertEquals(1.0, userCourse.getCompletion());
        assertEquals(0, userCourse.getIncorrectAttempts());

        assertTrue(userModule.isComplete());
        assertEquals(0, userModule.getIncorrectAttempts());
    }

    /**
     * 5 modules per course, 2 courses in program.
     */
    @Test
    public void testConversationProg2() {
        UserProgram userProgram = service.fetchProgram("pro3");
        UserCourse userCourse = service.fetchCourse("cou31");
        UserModule userModule = userCourse.getUserModule("mod311");

        userCourse = service.taskAttempt(userCourse.getId(), userModule.getId(), false);
        assertEquals(0.0, userCourse.getCompletion());
        assertEquals(1, userCourse.getIncorrectAttempts());

        userCourse = service.taskAttempt(userCourse.getId(), userModule.getId(), false);
        assertEquals(0.0, userCourse.getCompletion());
        assertEquals(CourseResult.COURSE_FAILED, userCourse.getCourseResult());
        userCourse = service.resetCourse(userCourse.getId());

        for (int i = 1; i < 6; i++) {
            userCourse = service.taskAttempt(userCourse.getId(), "mod31" + i, true);
            userModule = userCourse.getUserModule("mod31" + i);
            assertTrue(userModule.isComplete());
            assertEquals(0, userCourse.getIncorrectAttempts());
        }
        assertEquals(1.0, userCourse.getCompletion());
    }
}
