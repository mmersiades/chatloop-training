package org.chatloop.user_training.service.impl;

import com.contentful.java.cda.CDAContentType;
import com.contentful.java.cda.CDAEntry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleCDAEntry extends CDAEntry {
    private static class SimpleCDAContentType extends CDAContentType {
        private final String name;

        SimpleCDAContentType(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }
    }

    private final CDAContentType contentType;
    private final String id;
    private final Map<String, Object> fields = new HashMap<>();

    public SimpleCDAEntry(String id, String contentType) {
        this.id = id;
        this.contentType = new SimpleCDAContentType(contentType);
    }

    public SimpleCDAEntry(String contentType) {
        this(UUID.randomUUID().toString(), contentType);
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getField(String key) {
        return (T) fields.get(key);
    }

    public void setField(String key, Object value) {
        fields.put(key, value);
    }

    @Override
    public CDAContentType contentType() {
        return this.contentType;
    }
}
