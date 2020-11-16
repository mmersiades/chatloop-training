package org.chatloop.user_training.service.impl;

import org.apache.catalina.User;
import org.chatloop.user_training.IntegrationTestConfig;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.service.CourseProgressionService;
import org.chatloop.user_training.util.Fixture;
import org.chatloop.user_training.util.enums.UserIds;
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
    public void testProgressForCourse() {
        String userIdJohn = UserIds.JOHN.name();
        String userIdJane = UserIds.JANE.name();
        String courseA = "A";
        String courseB = "B";

        repository.save(Fixture.courseProgressFor(userIdJohn, courseA));
        assertEquals(1, service.progressForCourse(courseA).count());
        assertTrue(service.singleUserProgressForCourse(userIdJohn, courseA).isPresent());
        assertEquals(0, service.progressForCourse(courseB).count());

        repository.save(Fixture.courseProgressFor(userIdJane, courseA));
        repository.save(Fixture.courseProgressFor(userIdJohn, courseB));
        assertEquals(2, service.progressForCourse(courseA).count());
        assertEquals(1, service.progressForCourse(courseB).count());
    }

    @Test
    public void testUserProgressForCourse() {
        String john = UserIds.JOHN.name();
        String jane = UserIds.JANE.name();
        String luke = UserIds.LUKE.name();
        String course = "B";

        assertEquals(0, service.userProgressForCourse(course, john, jane, luke).count());
        repository.save(Fixture.courseProgressFor(john, course));
        repository.save(Fixture.courseProgressFor(jane, course));
        assertEquals(2, service.userProgressForCourse(course, john, jane, luke).count());
    }

    @Test
    public void testSingleUserProgressForCourse() {
        String billy = "billy";
        String course = "B";

        assertFalse(service.singleUserProgressForCourse(billy, course).isPresent());
        repository.save(Fixture.courseProgressFor(billy, course));
        assertTrue(service.singleUserProgressForCourse(billy, course).isPresent());
    }
}