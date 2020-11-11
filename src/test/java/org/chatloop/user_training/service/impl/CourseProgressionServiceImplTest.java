package org.chatloop.user_training.service.impl;

import org.chatloop.user_training.IntegrationTestConfig;
import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.service.CourseProgressionService;
import org.chatloop.user_training.util.Fixture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { IntegrationTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class CourseProgressionServiceImplTest {

    @Autowired
    private CourseProgressionService service;

    @Autowired
    private CourseProgressRepository repository;

    @Test
    public void testMarkCacheStale() {
        String userIdJohn = "john";
        String userIdJane = "jane";
        String courseA = "A";
        String courseB = "B";

        repository.save(Fixture.courseProgressFor(userIdJohn, courseA));
        assertEquals(1, service.userProgressForCourse(courseA).count());
        assertEquals(0, service.userProgressForCourse(courseB).count());

        repository.save(Fixture.courseProgressFor(userIdJane, courseA));
        repository.save(Fixture.courseProgressFor(userIdJohn, courseB));
        assertEquals(2, service.userProgressForCourse(courseA).count());
        assertEquals(1, service.userProgressForCourse(courseB).count());
    }
}