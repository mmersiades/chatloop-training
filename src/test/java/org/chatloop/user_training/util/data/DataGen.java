package org.chatloop.user_training.util.data;

import org.chatloop.user_training.model.progress.CourseProgress;
import org.chatloop.user_training.model.progress.CourseResult;
import org.chatloop.user_training.model.progress.ModuleProgress;

import java.util.UUID;

public class DataGen {

    public static CourseProgress getCourseProgress() {
        CourseProgress progress = new CourseProgress();
        progress.setCourseId("course" + id());
        progress.setUserId("user" + id());
        progress.setCourseResult(CourseResult.NO_RESULT_YET);
        return progress;
    }

    public static ModuleProgress getModuleProgress() {
        ModuleProgress moduleProgress = new ModuleProgress();
        moduleProgress.setModuleId("module" + id());
        moduleProgress.setIncorrectAttempts(0);
        return moduleProgress;
    }

    private static String id() {
        return UUID.randomUUID().toString();
    }
}
