package com.nkk.training.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Entity
@Table(name = "task")
public class Task extends IdAware {

    @NotBlank
    @Setter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @ManyToOne
    private Module module;
}