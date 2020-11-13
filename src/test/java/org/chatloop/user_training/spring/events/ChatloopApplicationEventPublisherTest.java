package org.chatloop.user_training.spring.events;

import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.repository.EarnedQualificationsRepository;
import org.chatloop.user_training.service.QualificationEvaluationService;
import org.chatloop.user_training.util.Fixture;
import org.chatloop.user_training.util.data.DataFactory;
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
    public void testOnApplicationEvent() {
        // john: nothing earned, qualifies for topicTexts
        courseProgressRepository.save(Fixture.courseProgressFor("john", DataFactory.COURSE_1));

        // jane: topicTexts earned, qualifies for topicTexts and twentyQuestions
        earnedQualificationsRepository.save(Fixture.earnedQualifications("jane", DataFactory.QUALIFICATION_TOPIC_TEXTS));
        courseProgressRepository.save(Fixture.courseProgressFor("jane", DataFactory.COURSE_1));
        courseProgressRepository.save(Fixture.courseProgressFor("jane", DataFactory.COURSE_2));

        // luke: topicTexts and twentyQuestions earned, qualifies for nothing (course 1 reset)
        earnedQualificationsRepository.save(Fixture.earnedQualifications("luke", DataFactory.QUALIFICATION_TOPIC_TEXTS, DataFactory.QUALIFICATION_TWENTY_QUESTIONS));
        courseProgressRepository.save(Fixture.courseProgressFor("luke", DataFactory.COURSE_2));

        // michael: topicTexts and twentyQuestions earned, qualifies for topicTexts and twentyQuestions (no changes)
        earnedQualificationsRepository.save(Fixture.earnedQualifications("michael", DataFactory.QUALIFICATION_TOPIC_TEXTS, DataFactory.QUALIFICATION_TWENTY_QUESTIONS));
        courseProgressRepository.save(Fixture.courseProgressFor("michael", DataFactory.COURSE_1));
        courseProgressRepository.save(Fixture.courseProgressFor("michael", DataFactory.COURSE_2));

        // triggers an evaluation and updates to the database
        publisher.publishEvent(new ContentUpdatedEvent(this));

        var earned = earnedQualificationsRepository.findAll();
        assertEquals(3, earned.size());

        var johnEarned = earned.stream()
                .filter(item -> item.getUserId().equals("john"))
                .collect(Collectors.toList());
        assertEquals(1, johnEarned.size());
        assertThat(johnEarned.get(0).getQualifications()).containsExactly(DataFactory.QUALIFICATION_TOPIC_TEXTS);

        var janeEarned = earned.stream()
                .filter(item -> item.getUserId().equals("jane"))
                .collect(Collectors.toList());
        assertEquals(1, janeEarned.size());
        assertThat(janeEarned.get(0).getQualifications()).containsExactly(DataFactory.QUALIFICATION_TOPIC_TEXTS, DataFactory.QUALIFICATION_TWENTY_QUESTIONS);

        var michaelEarned = earned.stream()
                .filter(item -> item.getUserId().equals("michael"))
                .collect(Collectors.toList());
        assertEquals(1, michaelEarned.size());
        assertThat(michaelEarned.get(0).getQualifications()).containsExactly(DataFactory.QUALIFICATION_TOPIC_TEXTS, DataFactory.QUALIFICATION_TWENTY_QUESTIONS);
    }

}