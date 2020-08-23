package com.chatloop.training.model.content;

import lombok.Getter;
import lombok.Setter;

/**
 * Root interface for all CDN-centric classes.
 *
 * Subclasses are generally 1 to 1 mapped with similar types defined in
 * Contentful CDN, ie our UserProgram is type program in Contentful. So
 * schema changes should be synchronized. Instance graph/tree fetched from
 * Contentful is then fused with, or enriched by, progress data that this
 * microservice owns and controls (see progress related model classes).
 *
 * So we have strict separation between read-only data owned and managed by
 * Contentful (this package) and mutable data our microservice owns. Latter
 * is "copy/pasted" on top of Contentful instances. This design of separation
 * of concerns is intentional. See comments in Transformer class for some
 * additional considerations for this design.
 */
@Getter
@Setter
public abstract class IdAware {

    private String id;

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

