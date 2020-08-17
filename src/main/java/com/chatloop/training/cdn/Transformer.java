package com.chatloop.training.cdn;

import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Module;
import com.chatloop.training.model.content.Program;
import com.chatloop.training.model.content.Task;
import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Ideally Contentful Java API would provide tooling to automatically generate
 * Java class model based on content model in their database. Alternatively, we
 * could do so since they export standard JSON API. However given simplicity of
 * our model in Contentful, and since we need small subset of their fields, it
 * does not seem worth the effort for now. Hence this simple transformation class.
 *
 * If requirements change dramatically in the future, might be worth to explore
 * then automatic mapping from JSON to Java class/object model etc.
 */
public class Transformer {

    public List<Program> toPrograms(Collection<CDAEntry> entries) {
        List<Program> programs = new ArrayList<>();
        for(CDAEntry entry : entries) {
            CDAContentType cdnType = entry.contentType();
            if(cdnType.name().equals("program")) {
                programs.add(toProgram(entry));
            }
        }
        return programs;
    }

    public Program toProgram(CDAEntry entry) {
        Program program = new Program();
        program.setId(entry.id());
        program.setName(entry.getField("name"));

        List<CDAEntry> courses = entry.getField("courses");
        for(CDAEntry courseEntry : courses) {
            Course course = toCourse(courseEntry);
            program.addCourse(course);
        }

        return program;
    }

    public Course toCourse(CDAEntry entry) {
        Course course = new Course();
        course.setId(entry.id());
        course.setName(entry.getField("name"));

        List<CDAEntry> modules = entry.getField("modules");
        for(CDAEntry moduleEntry : modules) {
            Module module = toModule(moduleEntry);
            course.addModule(module);
        }

        return course;
    }

    public Module toModule(CDAEntry entry) {
        Module module = new Module();
        module.setId(entry.id());
        module.setName(entry.getField("name"));

        CDAEntry taskEntry = entry.getField("moduleTask");
        module.setTask(toTask(taskEntry));

        return module;
    }

    public Task toTask(CDAEntry entry) {
        Task task = new Task();
        task.setId(entry.id());
        task.setName(entry.getField("name"));
        return task;
    }
}
