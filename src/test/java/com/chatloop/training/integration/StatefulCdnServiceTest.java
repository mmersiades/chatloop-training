package com.chatloop.training.integration;

import com.chatloop.training.CleanDbBeforeTests;
import com.chatloop.training.IntegrationTestConfig;
import com.chatloop.training.data.Known;
import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Program;
import com.chatloop.training.service.CdnService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IntegrationTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CleanDbBeforeTests
public class StatefulCdnServiceTest {

    @Autowired
    private CdnService cdnService;

    @Test
    public void testBasic() {
        Optional<Program> program = cdnService.fetchProgramById(Known.PROGRAM_ID);
        assertTrue(program.isPresent());
        assertEquals(Known.PROGRAM_ID, program.get().getId());

        Optional<Course> course = cdnService.fetchCourseById(Known.COURSE_ID);
        assertTrue(course.isPresent());
        assertEquals(Known.COURSE_ID, course.get().getId());
    }
}
