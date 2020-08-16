package com.nkk.training.model;

import javax.persistence.*;
import java.util.Objects;

@MappedSuperclass
public abstract class IdAware {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    protected IdAware() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdAware)) {
            return false;
        }
        IdAware that = (IdAware) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public String toString() {
        return "IdAware{" +
                "id=" + id +
                '}';
    }
}

