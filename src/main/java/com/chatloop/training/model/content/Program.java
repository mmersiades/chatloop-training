package com.chatloop.training.model.content;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public class Program extends IdAware {

    @Setter
    private String name;

    @Setter
    private String description;

    private final Set<Course> courses = new HashSet<>();

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public boolean containsCourse(Course course) {
        for(Course c : this.courses) {
            if(c.getId().equals(course.getId())) {
                return true;
            }
        }
        return false;
    }

    public Program copyWithoutChildren() {
        Program copy = new Program();
        copy.setId(getId());
        copy.setName(getName());
        copy.setDescription(getDescription());
        return copy;
    }
}