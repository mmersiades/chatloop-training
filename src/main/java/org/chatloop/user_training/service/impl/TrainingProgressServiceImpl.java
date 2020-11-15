package org.chatloop.user_training.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserModule;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.model.progress.CourseResult;
import org.chatloop.user_training.model.progress.ModuleProgress;
import org.chatloop.user_training.repository.CourseProgressRepository;
import org.chatloop.user_training.repository.ModuleProgressRepository;
import org.chatloop.user_training.service.CdnService;
import org.chatloop.user_training.service.ContentNotFound;
import org.chatloop.user_training.service.TrainingHistoryService;
import org.chatloop.user_training.service.TrainingProgressService;
import org.chatloop.user_training.spring.events.CourseResetEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
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
    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public List<UserProgram> fetchUserPrograms() {
        String userId = getUserId();

        List<UserProgram> userPrograms = cdnService.fetchUserPrograms();
        for (UserProgram userProgram : userPrograms) {
            if (userProgram.getUserCourseCnt() == 0) {
                log.warn("Program {}/{} with 0 courses, is Contentful model correct?", userProgram.getName(), userProgram.getId());
            }
            for (UserCourse userCourse : userProgram.getUserCourses()) {
                getOrCreateCourseProgressAndModuleProgresses(userId, userCourse);
                userCourse.calculateDerivedInfo();
            }
        }
        return userPrograms;
    }

    @Override
    public UserProgram fetchProgram(String programId) {
        String userId = getUserId();

        UserProgram userProgram = getProgramFromCdnStrict(programId);
        for (UserCourse userCourse : userProgram.getUserCourses()) {
            CourseProgress progress = getOrCreateCourseProgressAndModuleProgresses(userId, userCourse);
            updateCdnModelWithProgressInfo(userCourse, progress);
        }
        return userProgram;
    }

    @Override
    public UserCourse fetchCourse(String courseId) {
        String userId = getUserId();

        UserCourse userCourse = getCourseFromCdnStrict(courseId);
        CourseProgress progress = getOrCreateCourseProgressAndModuleProgresses(userId, userCourse);
        updateCdnModelWithProgressInfo(userCourse, progress);
        return userCourse;
    }

    @Override
    public UserCourse resetCourse(String courseId) {
        String userId = getUserId();

        UserCourse userCourse = getCourseFromCdnStrict(courseId);
        CourseProgress progress = getOrCreateCourseProgressAndModuleProgresses(userId, userCourse);

        resetCourseProgress(userCourse, progress);
        userCourse.setAttemptLimit(progress);

        CourseResetEvent event = new CourseResetEvent(userId, this);
        publisher.publishEvent(event);

        return userCourse;
    }

    @Override
    public UserCourse taskAttempt(String courseId, String moduleId, boolean isCorrect) {
        String userId = getUserId();

        UserCourse userCourse = getCourseFromCdnStrict(courseId);
        UserModule userModule = userCourse.getUserModule(moduleId);

        CourseProgress progress = getOrCreateCourseProgressAndModuleProgresses(userId, userCourse);
        ModuleProgress moduleProgress = progress.getModuleProgress(moduleId);
        moduleProgress.onAttempt(isCorrect);

        if (isCorrect) {
            historyService.onTaskCorrect(userId, courseId, moduleId);
            if (userCourse.isLast(moduleId)) {
                progress.setCourseResult(CourseResult.COURSE_PASSED);
                historyService.onCourseComplete(userId, courseId);
            }
        } else {
            historyService.onTaskIncorrect(userId, courseId, moduleId);
            if (isLimitExceeded(progress)) {
                progress.setCourseResult(CourseResult.COURSE_FAILED);
                historyService.onCourseFailed(userId, courseId);
                /*
                 * Based on discussion with UI team, disabling for now automatic course
                 * reset, but rather leaving it up to users/UI to invoke reset command.
                 */
//                resetCourseProgress(userCourse, progress);
            }
        }
        courseRepo.save(progress);
//        moduleRepo.save(moduleProgress);

        updateCdnModelWithProgressInfo(userCourse, progress);
        userCourse.setAttemptLimit(progress);

        return userCourse;
    }

    private void resetCourseProgress(UserCourse userCourse, CourseProgress progress) {
        String userId = getUserId();

        progress.setCourseResult(CourseResult.NO_RESULT_YET);

        // reset any present module as well to original state
        for (ModuleProgress moduleProgress : progress.getModuleProgresses()) {
            moduleProgress.setIncorrectAttempts(0);
            moduleProgress.setComplete(false);
        }
        // should cascade any changed module progress
        progress = courseRepo.save(progress);

        updateCdnModelWithProgressInfo(userCourse, progress);

        historyService.onCourseReset(userId, userCourse.getId());
    }

    /**
     * Only reads from DB, and updates CDN model with progress info. Assumes (for now)
     * that course/modules progress exist.
     */
    private void updateCdnModelWithProgressInfo(UserCourse userCourse, CourseProgress progress) {
        for (UserModule userModule : userCourse.getUserModules()) {
            ModuleProgress modProg = progress.getModuleProgress(userModule.getId());
            userModule.refreshFrom(modProg);
        }
        userCourse.setCourseResult(progress.getCourseResult());
        userCourse.calculateDerivedInfo();
        userCourse.setAttemptLimit(progress);
    }

    private boolean isLimitExceeded(CourseProgress progress) {
        LimitCalculator calc = new LimitCalculator();
        int limit = calc.getLimit(progress.getNumberOfModules());
        return progress.getIncorrectAttempts() > limit;
    }

    private UserCourse getCourseFromCdnStrict(String courseId) {
        Optional<UserCourse> course = cdnService.fetchCourseById(courseId);
        return course.orElseThrow(() -> {
            throw new ContentNotFound("Course ID not found in CDN " + courseId);
        });
    }

    private UserProgram getProgramFromCdnStrict(String programId) {
        Optional<UserProgram> program = cdnService.fetchProgramById(programId);
        return program.orElseThrow(() -> {
            throw new ContentNotFound("Program ID not found in CDN " + programId);
        });
    }

    /**
     * Creates course progress and module progresses in database for given user.
     * Also populates CDN object model with completion info based on history of
     * attempts stored in DB, if any.
     */
    private CourseProgress getOrCreateCourseProgressAndModuleProgresses(String userId, UserCourse userCourse) {
        CourseProgress courseProgress = getOrCreateCourseProgress(userId, userCourse.getId(), false);

        // if module progress for user/course does not exist, create it
        for (UserModule userModule : userCourse.getUserModules()) {
            ModuleProgress moduleProgress = courseProgress.getOrCreateModuleProgress(userModule.getId());
            userModule.setComplete(moduleProgress.isComplete());
            userModule.setIncorrectAttempts(moduleProgress.getIncorrectAttempts());
        }
        // cascade save all changes
        courseRepo.save(courseProgress);

        log.info("Course {} synchronized with DB for user {} in space {}, env {}", userCourse.getId(), userId, spaceId, envId);
        return courseProgress;
    }

    /**
     * Returns course progress if present in database, otherwise creates one.
     */
    private CourseProgress getOrCreateCourseProgress(String userId, String courseId, boolean doSave) {
        Optional<CourseProgress> progressOpt = courseRepo.findByCompositeId(spaceId, envId, userId, courseId);
        return progressOpt.orElseGet(() -> {
            CourseProgress progress = new CourseProgress();
            progress.setSpaceId(spaceId);
            progress.setEnvId(envId);
            progress.setUserId(userId);
            progress.setCourseId(courseId);
            if (doSave) {
                courseRepo.save(progress);
            }
            return progress;
        });
    }

    /**
     * Security context should be populated by JWT filter.
     */
    private String  getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        log.info("Extracted user ID {}", principal);
        return String.valueOf(principal);
    }
}
