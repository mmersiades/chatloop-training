package org.chatloop.user_training.model.content;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UserQualification extends IdAware {
    @Setter
    private String name;

    @Setter
    private Set<String> courseIds;

    @Override
    public String toString() {
        return new StringJoiner(", ", UserQualification.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("courseIds=" + courseIds)
                .toString();
    }

    public static UserQualification create(String qualificationName, String... requiredCoursesIds) {
        UserQualification qualification = new UserQualification();
        qualification.setName(qualificationName);
        qualification.setCourseIds(
                Arrays.stream(requiredCoursesIds).collect(Collectors.toSet())
        );
        return qualification;
    }
}
