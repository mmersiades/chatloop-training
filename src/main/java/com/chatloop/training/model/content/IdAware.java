package com.chatloop.training.model.content;

/**
 * Root interface for all CDN-centric classes.
 */
public abstract class IdAware {

    private String id;

    protected IdAware() {
    }

    public final String getId() {
        return id;
    }
    public final void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
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
        return getId().equals(that.getId());
    }

    @Override
    public String toString() {
        return "IdAware{" +
                "id=" + id +
                '}';
    }
}

