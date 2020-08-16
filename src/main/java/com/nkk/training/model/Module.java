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
@Table(name = "module")
public class Module extends IdAware {

    @NotBlank
    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @Column(name = "displayOrder", nullable = false)
    private int displayOrder;

    @Setter
    @ManyToOne
    private Course course;

    @Setter
    @OneToMany(mappedBy = "module", fetch = FetchType.EAGER)
    private Set<Task> tasks;
}
