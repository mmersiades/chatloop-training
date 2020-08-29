package org.chatloop.user_training.cdn;

import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;
import org.chatloop.user_training.model.content.UserCourse;
import org.chatloop.user_training.model.content.UserModule;
import org.chatloop.user_training.model.content.UserProgram;

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

    public List<UserProgram> toUserPrograms(Collection<CDAEntry> entries, boolean recursive) {
        List<UserProgram> userPrograms = new ArrayList<>();
        for (CDAEntry entry : entries) {
            CDAContentType cdnType = entry.contentType();
            if (cdnType.name().equals("program")) {
                userPrograms.add(toUserProgram(entry, recursive));
            }
        }
        return userPrograms;
    }

    /**
     * If true, subtree will be transformed as well, that is, courses.
     */
    public UserProgram toUserProgram(CDAEntry entry, boolean recursive) {
        UserProgram userProgram = new UserProgram();
        userProgram.setId(entry.id());
        userProgram.setName(entry.getField("name"));

        if (recursive) {
            List<CDAEntry> courses = entry.getField("courses");
            for (CDAEntry courseEntry : courses) {
                UserCourse userCourse = toUserCourse(courseEntry);
                userProgram.addUserCourse(userCourse);
            }
        }

        return userProgram;
    }

    public UserCourse toUserCourse(CDAEntry entry) {
        UserCourse userCourse = new UserCourse();
        userCourse.setId(entry.id());
        userCourse.setName(entry.getField("name"));

        List<CDAEntry> modules = entry.getField("modules");
        for (CDAEntry moduleEntry : modules) {
            UserModule userModule = toUserModule(moduleEntry);
            userCourse.addUserModule(userModule);
        }

        return userCourse;
    }

    public UserModule toUserModule(CDAEntry entry) {
        UserModule userModule = new UserModule();
        userModule.setId(entry.id());
        userModule.setName(entry.getField("name"));

        return userModule;
    }
}
