package org.chatloop.user_training.repository;

import org.chatloop.user_training.LocalTestConfig;
import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.model.progress.CourseResult;
import org.chatloop.user_training.model.progress.ModuleProgress;
import org.chatloop.user_training.util.TestUtil;
import org.chatloop.user_training.util.data.DataGen;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes = LocalTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:db_scripts/delete_all.sql")
public class UserCourseProgressRepositoryTest {

    @Autowired
    private CourseProgressRepository courseProgressRepository;

    @Test
    public void testCourseProgressInsertQuery() {
        CourseProgress progress = DataGen.getCourseProgress();

        progress = courseProgressRepository.save(progress);

        CourseProgress queried = courseProgressRepository.findById(progress.getId()).get();

        TestUtil.assertCourseProgressEqual(progress, queried);

        progress.setCourseResult(CourseResult.COURSE_PASSED);
        courseProgressRepository.save(progress);
        queried = courseProgressRepository.findById(progress.getId()).get();

        TestUtil.assertCourseProgressEqual(progress, queried);
    }

    @Test
    public void testFindComposite() {
        CourseProgress progress = DataGen.getCourseProgress();
        ModuleProgress moduleProgress = DataGen.getModuleProgress();
        progress.addModuleProgress(moduleProgress);

        progress = courseProgressRepository.save(progress);

        CourseProgress queried = courseProgressRepository.findById(progress.getId()).get();

        TestUtil.assertCourseProgressEqual(progress, queried);
        TestUtil.assertModuleProgressEqual(moduleProgress, queried.getModuleProgress(moduleProgress.getModuleId()));
    }
}
