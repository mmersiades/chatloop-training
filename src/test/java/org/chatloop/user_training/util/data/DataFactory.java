package org.chatloop.user_training.util.data;

import org.assertj.core.util.Lists;
import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserModule;
import org.chatloop.user_training.model.content.UserProgram;
import org.chatloop.user_training.model.content.UserQualification;

import java.util.Collection;
import java.util.Collections;

/**
 * Used to generate hierarchical data for testing with fake CDN service.
 */
public final class DataFactory {

    public static final String COURSE_1 = "cou1";
    public static final String COURSE_2 = "cou2";

    /**
     * Program with single course with single module.
     *
     * pro1/
     *      cou1/
     *          mod1/
     */
    public UserProgram createProgram1() {
        UserProgram userProgram = new UserProgram();
        userProgram.setId("pro1");
        userProgram.setName("Program 1");

        UserCourse userCourse = new UserCourse();
        userCourse.setId(DataFactory.COURSE_1);
        userCourse.setName("Course 1");
        userProgram.addUserCourse(userCourse);

        UserModule userModule = new UserModule();
        userModule.setId("mod1");
        userModule.setName("Module 1");
        userCourse.addUserModule(userModule);

        return userProgram;
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
    public UserProgram createProgram2() {
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
    public UserProgram createProgram3() {
        return createProgram(2, 5, "3");
    }

    private UserProgram createProgram(int courseCnt, int modulesPerCourse, String prefix) {
        UserProgram userProgram = new UserProgram();
        userProgram.setId("pro" + prefix);
        userProgram.setName("Program " + prefix);

        for (int i = 1; i < courseCnt + 1; i++) {
            UserCourse userCourse = new UserCourse();
            userCourse.setId("cou" + prefix + i);
            userCourse.setName("Course " + prefix + i);
            userProgram.addUserCourse(userCourse);

            for (int j = 1; j < modulesPerCourse + 1; j++) {
                UserModule userModule = new UserModule();
                userModule.setId("mod" + prefix + i + "" + j);
                userModule.setName("Module " + prefix + i + "" + j);
                userCourse.addUserModule(userModule);
            }
        }

        return userProgram;
    }

    /**
     * Returns two {@link UserQualification}s, one called {@code doTopicTexts} the other {@code covid19}.
     * @return List<UserQualification>
     */
    public Collection<UserQualification> userQualifications() {
        UserQualification doTopicTexts = UserQualification.create("doTopicTexts", DataFactory.COURSE_1);
        UserQualification covid19 = UserQualification.create("covid19", DataFactory.COURSE_1, DataFactory.COURSE_2);
        return Lists.list(doTopicTexts, covid19);
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
