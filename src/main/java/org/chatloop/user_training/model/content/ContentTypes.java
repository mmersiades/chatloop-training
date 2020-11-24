package org.chatloop.user_training.model.content;

import com.contentful.java.cda.CDAEntry;

import java.util.ArrayList;
import java.util.List;

public enum ContentTypes {
    QUALIFICATION("qualification"),
    PROGRAM("program"),
    COURSE("course"),
    MODULE("module");

    private final String name;

    ContentTypes(String name) {
        this.name = name;
    }

    public boolean supports(CDAEntry entry) {
        return entry.contentType().name().equals(this.name);
    }

    private static final List<String> contentTypeNames;

    static {
        ContentTypes[] values = ContentTypes.values();
        List<String> names = new ArrayList<>(values.length);
        for (ContentTypes value : values) {
            names.add(value.name);
        }
        contentTypeNames = names;
    }

    public static List<String> allContentTypes() {
        return contentTypeNames;
    }
}
