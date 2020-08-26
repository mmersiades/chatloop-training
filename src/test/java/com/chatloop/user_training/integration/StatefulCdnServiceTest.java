package com.chatloop.user_training.integration;

import com.chatloop.user_training.IntegrationTestConfig;
import com.chatloop.user_training.model.content.UserCourse;
import com.chatloop.user_training.model.content.UserProgram;
import com.chatloop.user_training.service.CdnService;
import com.chatloop.user_training.util.data.Known;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class StatefulCdnServiceTest {

    @Autowired
    private CdnService cdnService;

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
