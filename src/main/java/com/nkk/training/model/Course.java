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
@Table(name = "course")
public class Course extends IdAware {

    @NotBlank
    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @Column(name = "description", nullable = true)
    private String description;

    @Setter
    @Column(name = "displayOrder", nullable = false)
    private int displayOrder;

    @Setter
    @Column(name = "completion", nullable = false)
    private float completion;

    @Setter
    @ManyToOne
    private Program program;

    @Setter
    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
    private Set<Module> modules;
}
