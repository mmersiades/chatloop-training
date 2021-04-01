package org.chatloop.user_training.service.impl;

import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.model.progress.EarnedQualifications;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.service.QualificationEvaluationService;
import org.chatloop.user_training.util.Fixture;
import org.chatloop.user_training.util.enums.Courses;
import org.chatloop.user_training.util.enums.Qualifications;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LocalTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
class QualificationEvaluationServiceImplTest {

    @Autowired
    private QualificationEvaluationService service;

    @Autowired
    private CourseProgressRepository courseProgressRepository;

    @Test
    public void testEvaluateUsers() {
        String fred = "fred";
        String marlen = "marlen";
        List<EarnedQualifications> withoutAnyProgress = service
                .evaluateUsers(fred, marlen)
                .asEarnedQualifications()
                .collect(Collectors.toList());
        assertTrue(withoutAnyProgress.isEmpty());

        courseProgressRepository.save(Fixture.courseProgressFor(fred, Courses.COU1.name()));
        courseProgressRepository.save(Fixture.courseProgressFor(marlen, Courses.COU1.name()));
        courseProgressRepository.save(Fixture.courseProgressFor(fred, Courses.COU2.name()));

        List<EarnedQualifications> withCourseProgress = service
                .evaluateUsers(fred, marlen)
                .asEarnedQualifications()
                .collect(Collectors.toList());
        assertEquals(2, withCourseProgress.size());

        List<EarnedQualifications> twoQualifications = withCourseProgress
                .stream()
                .filter(earnedQualifications -> earnedQualifications.getQualifications().size() == 2)
                .collect(Collectors.toList());
        assertEquals(1, twoQualifications.size());

        for (EarnedQualifications eqf : twoQualifications) {
            assertEquals(eqf.getUserId(), "fred");
            assertThat(eqf.getQualifications()).hasSameElementsAs(Sets.newSet(Qualifications.TOPIC_TEXTS.name(), Qualifications.TWENTY_QUESTIONS.name()));
        }
    }

    @Test
    public void testEvaluateAllUsers() {
        List<EarnedQualifications> withoutAnyProgress = service
                .evaluateAllUsers()
                .asEarnedQualifications()
                .collect(Collectors.toList());
        assertTrue(withoutAnyProgress.isEmpty());

        // 1000 users have passed cou1
        courseProgressRepository.saveAll(
                IntStream.range(0, 1000)
                    .mapToObj(i -> {
                        var userId = String.format("usr%s", i);
                        return Fixture.courseProgressFor(userId, Courses.COU1.name());
                    }).collect(Collectors.toList())
        );

        // 2 users have passed cou2
        IntStream.range(300, 302)
                .forEach(i -> {
                    var userId = String.format("usr%s", i);
                    courseProgressRepository.save(Fixture.courseProgressFor(userId, Courses.COU2.name()));
                });

        List<EarnedQualifications> withCourseProgress = service
                .evaluateAllUsers()
                .asEarnedQualifications()
                .collect(Collectors.toList());
        assertEquals(1000, withCourseProgress.size());

        List<EarnedQualifications> twoQualifications = withCourseProgress
                .stream()
                .filter(earnedQualifications -> earnedQualifications.getQualifications().size() == 2)
                .collect(Collectors.toList());
        assertEquals(2, twoQualifications.size());

        for (EarnedQualifications eqf : twoQualifications) {
            assertTrue(eqf.getUserId().equals("usr300") || eqf.getUserId().equals("usr301"));
            assertThat(eqf.getQualifications()).hasSameElementsAs(Sets.newSet(Qualifications.TOPIC_TEXTS.name(), Qualifications.TWENTY_QUESTIONS.name()));
        }
    }
}