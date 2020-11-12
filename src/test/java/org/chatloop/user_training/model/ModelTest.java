package org.chatloop.user_training.model;

import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserModule;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.util.data.DataFactory;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    public void testUserProgram() {
        UserProgram program = new UserProgram();
        UserCourse course = new UserCourse();
        course.setId(DataFactory.COURSE_1);

        UserModule module = new UserModule();
        module.setId("mod1");
        course.addUserModule(module);

        program.addUserCourse(course);

        assertTrue(program.containsCourse(course));

        UserProgram copy = program.copyWithoutChildren();
        assertEquals(copy.getId(), program.getId());
        assertFalse(copy.containsCourse(course));

        assertSame(module, course.getUserModule("mod1"));
    }
}
