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
    private final String id = UUID.randomUUID().toString();
    private final Map<String, Object> fields = new HashMap<>();

    public SimpleCDAEntry(String contentType) {
        this.contentType = new SimpleCDAContentType(contentType);
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
