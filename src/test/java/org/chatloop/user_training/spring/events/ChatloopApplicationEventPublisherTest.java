package org.chatloop.user_training.spring.events;

import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.model.progress.CourseResult;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.repository.EarnedQualificationsRepository;
import org.chatloop.user_training.service.QualificationEvaluationService;
import org.chatloop.user_training.util.Fixture;
import org.chatloop.user_training.util.data.DataFactory;
import org.chatloop.user_training.util.enums.Courses;
import org.chatloop.user_training.util.enums.Qualifications;
import org.chatloop.user_training.util.enums.UserIds;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LocalTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
class ChatloopApplicationEventPublisherTest {

    @Autowired
    private ChatloopApplicationEventPublisher publisher;

    @Autowired
    private CourseProgressRepository courseProgressRepository;

    @Autowired
    private EarnedQualificationsRepository earnedQualificationsRepository;

    @Test
    public void testOnContentUpdatedEvent() {
        // john: nothing earned, qualifies for topicTexts
        courseProgressRepository.save(Fixture.courseProgressFor(UserIds.JOHN.name(), Courses.COU1.name()));

        // jane: topicTexts earned, qualifies for topicTexts and twentyQuestions
        earnedQualificationsRepository.save(Fixture.earnedQualifications(UserIds.JANE.name(), Qualifications.TOPIC_TEXTS.name()));
        courseProgressRepository.save(Fixture.courseProgressFor(UserIds.JANE.name(), Courses.COU1.name()));
        courseProgressRepository.save(Fixture.courseProgressFor(UserIds.JANE.name(), Courses.COU2.name()));

        // luke: topicTexts and twentyQuestions earned, qualifies for nothing (course 1 reset)
        earnedQualificationsRepository.save(Fixture.earnedQualifications(UserIds.LUKE.name(), Qualifications.TOPIC_TEXTS.name(), Qualifications.TWENTY_QUESTIONS.name()));
        courseProgressRepository.save(Fixture.courseProgressFor(UserIds.LUKE.name(), Courses.COU2.name()));

        // michael: topicTexts and twentyQuestions earned, qualifies for topicTexts and twentyQuestions (no changes)
        earnedQualificationsRepository.save(Fixture.earnedQualifications(UserIds.MICHAEL.name(), Qualifications.TOPIC_TEXTS.name(), Qualifications.TWENTY_QUESTIONS.name()));
        courseProgressRepository.save(Fixture.courseProgressFor(UserIds.MICHAEL.name(), Courses.COU1.name()));
        courseProgressRepository.save(Fixture.courseProgressFor(UserIds.MICHAEL.name(), Courses.COU2.name()));

        // triggers an evaluation and updates to the database
        publisher.publishEvent(new ContentUpdatedEvent(this));

        var earned = earnedQualificationsRepository.findAll();
        assertEquals(3, earned.size());

        var johnEarned = earned.stream()
                .filter(item -> item.getUserId().equals(UserIds.JOHN.name()))
                .collect(Collectors.toList());
        assertEquals(1, johnEarned.size());
        assertThat(johnEarned.get(0).getQualifications()).containsExactlyInAnyOrder(Qualifications.TOPIC_TEXTS.name());

        var janeEarned = earned.stream()
                .filter(item -> item.getUserId().equals(UserIds.JANE.name()))
                .collect(Collectors.toList());
        assertEquals(1, janeEarned.size());
        assertThat(janeEarned.get(0).getQualifications()).containsExactlyInAnyOrder(Qualifications.TOPIC_TEXTS.name(), Qualifications.TWENTY_QUESTIONS.name());

        var michaelEarned = earned.stream()
                .filter(item -> item.getUserId().equals(UserIds.MICHAEL.name()))
                .collect(Collectors.toList());
        assertEquals(1, michaelEarned.size());
        assertThat(michaelEarned.get(0).getQualifications()).containsExactlyInAnyOrder(Qualifications.TOPIC_TEXTS.name(), Qualifications.TWENTY_QUESTIONS.name());
    }

    @Test
    public void testOnCourseResetEvent() {
        // lars: topicTexts and twentyQuestions earned, qualifies for nothing (course 1 reset)
        String lars = UserIds.LARS.name();
        earnedQualificationsRepository.save(Fixture.earnedQualifications(lars, Qualifications.TOPIC_TEXTS.name(), Qualifications.TWENTY_QUESTIONS.name()));
        courseProgressRepository.save(Fixture.courseProgressFor(lars, Courses.COU1.name(), CourseResult.NO_RESULT_YET));
        courseProgressRepository.save(Fixture.courseProgressFor(lars, Courses.COU2.name()));

        // triggers an evaluation and updates to the database
        publisher.publishEvent(new CourseResetEvent(lars, this));

        var earned = earnedQualificationsRepository.findById(lars);
        assertFalse(earned.isPresent());
    }

    @Test
    public void testOnCoursePassedEvent() {
        // bill: earned nothing, qualifies for topicTexts (course 1 passed)
        String bill = UserIds.BILL.name();
        courseProgressRepository.save(Fixture.courseProgressFor(bill, Courses.COU1.name()));

        // triggers an evaluation and updates to the database
        publisher.publishEvent(new CoursePassedEvent(bill, this));

        var earned = earnedQualificationsRepository.findById(bill);
        assertTrue(earned.isPresent());
    }
}