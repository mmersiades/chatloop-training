package com.chatloop.training.data;

import com.chatloop.training.model.content.Course;
import com.chatloop.training.model.content.Module;
import com.chatloop.training.model.content.Program;

/**
 * Used to generate hierarchical data for testing with face CDN service.
 */
public final class DataFactory {

    /**
     * Program with single course with single module.
     *
     * pro1/
     *      cou1/
     *          mod1/
     */
    public Program createProgram1() {
        Program program = new Program();
        program.setId("pro1");
        program.setName("Program 1");

        Course course = new Course();
        course.setId("cou1");
        course.setName("Course 1");
        program.addCourse(course);

        Module module = new Module();
        module.setId("mod1");
        module.setName("Module 1");
        course.addModule(module);

        return program;
    }

    /**
     * Program with 2 courses, each with 2 modules.
     *
     * pro2/
     *      cou21/
     *          mod211/
     *          mod212/
     *      cou22/
     *          mod221/
     *          mod222/
     */
    public Program createProgram2() {
        return createProgram(2, 2, "2");
    }

    /**
     * Program with 3 courses, each with 5 modules.
     *
     * pro3/
     *      cou31/
     *          mod311/
     *          mod312/
     *          mod313/
     *          mod314/
     *          mod315/
     *      cou32/
     *          ...
     */
    public Program createProgram3() {
        return createProgram(2, 5, "3");
    }

    private Program createProgram(int courseCnt, int modulesPerCourse, String prefix) {
        Program program = new Program();
        program.setId("pro" + prefix);
        program.setName("Program " + prefix);

        for(int i=1; i < courseCnt+1; i++) {
            Course course = new Course();
            course.setId("cou" + prefix + i);
            course.setName("Course " + prefix + i);
            program.addCourse(course);

            for(int j=1; j < modulesPerCourse+1; j++) {
                Module module = new Module();
                module.setId("mod" + prefix + i + "" + j);
                module.setName("Module " + prefix + i + "" + j);
                course.addModule(module);
            }
        }

        return program;
    }

//    public static void main(String[] args) {
//        DataFactory factory = new DataFactory();
//
//        Program prog1 = factory.createProgram1();
//        Program prog2 = factory.createProgram2();
//        Program prog3 = factory.createProgram3();
//
//        System.out.println("");
//    }
}
