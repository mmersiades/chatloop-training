package com.nkk.training.resolver;

import com.nkk.training.model.Course;
import com.nkk.training.model.Module;
import com.nkk.training.model.Program;
import com.nkk.training.model.Task;
import com.nkk.training.service.CourseService;
import com.nkk.training.service.ModuleService;
import com.nkk.training.service.ProgramService;
import com.nkk.training.service.TaskService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityQueryResolver implements GraphQLQueryResolver {

    private final ProgramService programService;
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final TaskService taskService;

    public EntityQueryResolver(ProgramService programService, CourseService courseService,
                               ModuleService moduleService, TaskService taskService) {
        this.programService = programService;
        this.courseService = courseService;
        this.moduleService = moduleService;
        this.taskService = taskService;
    }

    public Program getProgramById(Long id) {
        try {
            Optional<Program> program = this.programService.findOne(id);
            if (program.isPresent()) {
                return program.get();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public Course getCourseById(Long id) {
        try {
            Optional<Course> course = this.courseService.findOne(id);
            if (course.isPresent()) {
                return course.get();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public Module getModuleById(Long id) {
        try {
            Optional<Module> module = this.moduleService.findOne(id);
            if (module.isPresent()) {
                return module.get();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    public Task getTaskById(Long id) {
        try {
            Optional<Task> task = this.taskService.findOne(id);
            if (task.isPresent()) {
                return task.get();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }
}
