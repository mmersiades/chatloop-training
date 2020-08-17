package com.chatloop.training;

import com.chatloop.training.model.event.TrainingEvent;
import com.chatloop.training.model.progress.CourseProgress;
import com.chatloop.training.model.progress.ModuleProgress;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtil {

    public static String randStr() {
        return UUID.randomUUID().toString();
    }

    public static void assertTrainingEventEqual(TrainingEvent expected, TrainingEvent actual) {
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getCourseId(), actual.getCourseId());
        assertEquals(expected.getModuleId(), actual.getModuleId());
        assertEquals(expected.getTrainingEventType(), actual.getTrainingEventType());
    }

    public static void assertCourseProgressEqual(CourseProgress expected, CourseProgress actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getCourseId(), actual.getCourseId());
        assertEquals(expected.getCourseResult(), actual.getCourseResult());
        assertEquals(expected.getIncorrectAttempts(), actual.getIncorrectAttempts());
        assertEquals(expected.getNumberOfModules(), actual.getNumberOfModules());
        // TODO compare modules as well
    }

    public static void assertModuleProgressEqual(ModuleProgress expected, ModuleProgress actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getModuleId(), actual.getModuleId());
        assertEquals(expected.getIncorrectAttempts(), actual.getIncorrectAttempts());
    }
}
