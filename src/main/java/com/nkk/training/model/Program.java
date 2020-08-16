package com.nkk.training.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Entity
@Table(name = "program")
public class Program extends IdAware {

    @NotBlank
    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @Column(name = "description", nullable = true)
    private String description;

    @Setter
    @OneToMany(mappedBy = "program", fetch = FetchType.EAGER)
    private Set<Course> courses;
}