package com.chatloop.training.repository;

import com.chatloop.training.CleanDbBeforeTests;
import com.chatloop.training.TestUtil;
import com.chatloop.training.data.DataGen;
import com.chatloop.training.model.progress.CourseProgress;
import com.chatloop.training.model.progress.CourseResult;
import com.chatloop.training.model.progress.ModuleProgress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@CleanDbBeforeTests
public class CourseProgressRepositoryTest {

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
