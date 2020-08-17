package com.chatloop.training.service.impl;

import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Program;
import com.chatloop.training.model.progress.CourseResult;
import com.chatloop.training.repository.CourseProgressRepository;
import com.chatloop.training.repository.ModuleProgressRepository;
import com.chatloop.training.model.content.Module;
import com.chatloop.training.model.progress.CourseProgress;
import com.chatloop.training.model.progress.ModuleProgress;
import com.chatloop.training.service.CdnService;
import com.chatloop.training.service.ContentNotFound;
import com.chatloop.training.service.TrainingHistoryService;
import com.chatloop.training.service.TrainingProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class TrainingProgressServiceImpl implements TrainingProgressService {

    @Value("${contentful.spaceId}")
    private String spaceId;
    @Value("${contentful.envId}")
    private String envId;

    @Autowired
    private CdnService cdnService;
    @Autowired
    private TrainingHistoryService historyService;
    @Autowired
    private CourseProgressRepository courseRepo;
    @Autowired
    private ModuleProgressRepository moduleRepo;

    private static String USER_ID = "user1"; // TODO FIXME

    /**
     * For now we assume this is always called first, thus ensuring that DB
     * always contains all courses/modules progresses for each user. TODO relax this requirement
     */
    @Override
    public Program fetchProgram(String programId) {
        Program program = getProgramFromCdnStrict(programId);
        for(Course course : program.getCourses()) {
            createProgressInfoInDb(USER_ID, course);
            course.calculateDerivedInfo();
        }
        return program;
    }

    @Override
    public Course fetchCourse(String courseId) {
        Course course = getCourseFromCdnStrict(courseId);
        CourseProgress progress = courseRepo.findByUserIdCourseId(USER_ID, courseId).get();
        updateCdnModelWithProgressInfo(course, progress);
        return course;
    }

    @Override
    public Course resetCourse(String courseId) {
        Course course = getCourseFromCdnStrict(courseId);
        CourseProgress progress = courseRepo.findByUserIdCourseId(USER_ID, courseId).get();

        resetCourseProgress(course, progress);

        return course;
    }

    @Override
    public Course taskAttempt(String courseId, String moduleId, boolean isCorrect) {
        Course course = getCourseFromCdnStrict(courseId);
        Module module = course.getModule(moduleId);

        CourseProgress progress = courseRepo.findByUserIdCourseId(USER_ID, courseId).get();
        ModuleProgress moduleProgress = progress.getModuleProgress(moduleId);
        moduleProgress.onAttempt(isCorrect);

        if(isCorrect) {
            historyService.onTaskCorrect(USER_ID, courseId, moduleId);
            if(course.isLast(moduleId)) {
                progress.setCourseResult(CourseResult.COURSE_PASSED);
                historyService.onCourseComplete(USER_ID, courseId);
            }
        } else {
            historyService.onTaskIncorrect(USER_ID, courseId, moduleId);
            if(isLimitExceeded(progress)) {
                historyService.onCourseFailed(USER_ID, courseId);
                resetCourseProgress(course, progress);
            }
        }
        courseRepo.save(progress);
        moduleRepo.save(moduleProgress);

        updateCdnModelWithProgressInfo(course, progress);

        return course;
    }

    private void resetCourseProgress(Course course, CourseProgress progress) {
        progress.setCourseResult(CourseResult.NO_RESULT_YET);

        // reset any present module as well to original state
        for(ModuleProgress moduleProgress : progress.getModules()) {
            moduleProgress.setIncorrectAttempts(0);
            moduleProgress.setComplete(false);
        }
        // should cascade any changed module progress
        progress = courseRepo.save(progress);

        updateCdnModelWithProgressInfo(course, progress);

        historyService.onCourseReset(USER_ID, course.getId());
    }

    /**
     * Creates course progress and module progresses in database for given user.
     * Also populates CDN object model with completion info based on history of
     * attempts stored in DB, if any.
     */
    private void createProgressInfoInDb(String userId, Course course) {
        Optional<CourseProgress> progressOpt = courseRepo.findByUserIdCourseId(userId, course.getId());

        // if course progress for user does not exist, create it
        CourseProgress couProg = progressOpt.orElseGet(() -> {
            CourseProgress prog = new CourseProgress();
            prog.setSpaceId(spaceId);
            prog.setEnvId(envId);
            prog.setUserId(userId);
            prog.setCourseId(course.getId());
            prog.setCourseResult(CourseResult.NO_RESULT_YET);
            return courseRepo.save(prog);
        });

        // if module progress for user/course does not exist, create it
        for(Module module : course.getModules()) {
            ModuleProgress modProg = couProg.getOrCreateModuleProgress(module.getId());
            modProg = moduleRepo.save(modProg);
            module.setComplete(modProg.isComplete());
            module.setIncorrectAttempts(modProg.getIncorrectAttempts());
        }

        log.info("Course {} synchronized with DB for user {}", course.getId(), userId);
    }

    /**
     * Only reads from DB, and updates CDN model with progress info. Assumes (for now)
     * that course/modules progress exist.
     */
    private void updateCdnModelWithProgressInfo(Course course, CourseProgress progress) {
        for(Module module : course.getModules()) {
            ModuleProgress modProg = progress.getModuleProgress(module.getId());
            module.refreshFrom(modProg);
        }
        course.setCourseResult(progress.getCourseResult());
        course.calculateDerivedInfo();
    }

    private boolean isLimitExceeded(CourseProgress progress) {
        LimitCalculator calc = new LimitCalculator();
        int limit = calc.getLimit(progress.getNumberOfModules());
        return progress.getIncorrectAttempts() > limit;
    }

    private Course getCourseFromCdnStrict(String courseId) {
        Optional<Course> course = cdnService.fetchCourseById(courseId);
        return course.orElseThrow(() -> {
            throw new ContentNotFound("Course ID not found in CDN " + courseId);
        });
    }

    private Program getProgramFromCdnStrict(String programId) {
        Optional<Program> program = cdnService.fetchProgramById(programId);
        return program.orElseThrow(() -> {
            throw new ContentNotFound("Program ID not found in CDN " + programId);
        });
    }

    private final Logger log = LoggerFactory.getLogger(TrainingProgressServiceImpl.class);
}
