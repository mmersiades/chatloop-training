package org.chatloop.user_training.service.impl;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.SynchronizedSpace;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;

import java.util.Map;

/**
 * A {@link StatefulCdnService} which can be primed with test data and act as a stub.
 */
public class StatefulCdnServiceStub extends StatefulCdnService {
    private static class StubbedSynchronizedSpace extends SynchronizedSpace {
        private final Map<String, CDAEntry> entries;

        StubbedSynchronizedSpace(Map<String, CDAEntry> entries) {
            this.entries = entries;
        }

        @Override
        public Map<String, CDAEntry> entries() {
            return this.entries;
        }
    }

    private final SynchronizedSpace stubbedSpace;

    public StatefulCdnServiceStub(Transformer transformer, ChatloopApplicationEventPublisher applicationEventPublisher, Map<String, CDAEntry> entries) {
        super(transformer, applicationEventPublisher);
        this.stubbedSpace = new StubbedSynchronizedSpace(entries);
    }

    @Override
    protected void syncSchema() { }

    @Override
    protected SynchronizedSpace syncData() {
        return stubbedSpace;
    }

    @Override
    protected CDAClient createClient() {
        return null;
    }
}
