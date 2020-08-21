package com.chatloop.training.service;

import com.chatloop.training.CleanDbBeforeTests;
import com.chatloop.training.LocalTestConfig;
import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Module;
import com.chatloop.training.model.content.Program;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {LocalTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CleanDbBeforeTests
public class TrainingProgressServiceTest {

    @Autowired
    private TrainingProgressService service;

    @Test
    public void testBasicQuery() {
        Program program = service.fetchProgram("pro1");
        Course course = service.fetchCourse("cou1");
        Module module = course.getModule("mod1");

        assertEquals("pro1", program.getId());
        assertEquals("cou1", course.getId());
        assertEquals("mod1", module.getId());
    }

    @Test
    public void testConversationProg1() {
        Program program = service.fetchProgram("pro1");
        Course course = service.fetchCourse("cou1");
        Module module = course.getModule("mod1");

        course = service.taskAttempt(course.getId(), module.getId(), true);
        module = course.getModule("mod1");

        assertEquals(1.0, course.getCompletion());
        assertEquals(0, course.getIncorrectAttempts());

        assertTrue(module.isComplete());
        assertEquals(0, module.getIncorrectAttempts());
    }

    /**
     * 5 modules per course, 2 courses in program.
     */
    @Test
    public void testConversationProg2() {
        Program program = service.fetchProgram("pro3");
        Course course = service.fetchCourse("cou31");
        Module module = course.getModule("mod311");

        course = service.taskAttempt(course.getId(), module.getId(), false);
        assertEquals(0.0, course.getCompletion());
        assertEquals(1, course.getIncorrectAttempts());

        course = service.taskAttempt(course.getId(), module.getId(), false);
        assertEquals(0.0, course.getCompletion());

        for(int i=1; i < 6; i++) {
            course = service.taskAttempt(course.getId(), "mod31" + i, true);
            module = course.getModule("mod31" + i);
            assertTrue(module.isComplete());
            assertEquals(0, course.getIncorrectAttempts());
        }
        assertEquals(1.0, course.getCompletion());
    }
}
