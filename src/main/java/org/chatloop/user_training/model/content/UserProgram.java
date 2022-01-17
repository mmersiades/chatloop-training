package org.chatloop.user_training.model.content;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class UserProgram extends IdAware {

    @Setter
    private String name;

    @Setter
    private String description;

    private final List<UserCourse> userCourses = new ArrayList<>();

    public void addUserCourse(UserCourse userCourse) {
        this.userCourses.add(userCourse);
    }

    public boolean containsCourse(UserCourse userCourse) {
        for (UserCourse c : this.userCourses) {
            if (c.getId().equals(userCourse.getId())) {
                return true;
            }
        }
        return false;
    }

    public UserProgram copyWithoutChildren() {
        UserProgram copy = new UserProgram();
        copy.setId(getId());
        copy.setName(getName());
        copy.setDescription(getDescription());
        return copy;
    }

    public int getUserCourseCnt() {
        return userCourses.size();
    }
}