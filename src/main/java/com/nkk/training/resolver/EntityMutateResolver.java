package com.nkk.training.resolver;

import com.nkk.training.model.Course;
import com.nkk.training.model.Module;
import com.nkk.training.model.Program;
import com.nkk.training.model.Task;
import com.nkk.training.service.CourseService;
import com.nkk.training.service.ModuleService;
import com.nkk.training.service.ProgramService;
import com.nkk.training.service.TaskService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import org.springframework.stereotype.Component;

@Component
public class EntityMutateResolver implements GraphQLMutationResolver {

    private final ProgramService programService;
    private final CourseService courseService;
    private final ModuleService moduleService;
    private final TaskService taskService;

    public EntityMutateResolver(ProgramService programService, CourseService courseService,
                                ModuleService moduleService, TaskService taskService) {
        this.programService = programService;
        this.courseService = courseService;
        this.moduleService = moduleService;
        this.taskService = taskService;
    }

    public Program addProgram(String title, String description) throws Exception {
        Program program = new Program();
        program.setTitle(title);
        program.setDescription(description);
        return this.programService.save(program);
    }

    public Course addCourse(String title, String description,
                            int displayOrder, float completion,
                            long programId) throws Exception {

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setDisplayOrder(displayOrder);
        course.setCompletion(completion);
        course.setProgram(this.programService.findOne(programId).get());
        return this.courseService.save(course);
    }

    public Module addModule(String title, int displayOrder, long courseId) throws Exception {
        Module module = new Module();
        module.setTitle(title);
        module.setDisplayOrder(displayOrder);
        module.setCourse(this.courseService.findOne(courseId).get());
        return this.moduleService.save(module);
    }

    public Task addTask(String title, long moduleId) throws Exception {
        Task task = new Task();
        task.setTitle(title);
        task.setModule(this.moduleService.findOne(moduleId).get());
        return this.taskService.save(task);
    }
}
