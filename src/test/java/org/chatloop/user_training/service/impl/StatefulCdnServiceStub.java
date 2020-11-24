package org.chatloop.user_training.service.impl;

import com.contentful.java.cda.CDAClient;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.SynchronizedSpace;
import org.chatloop.user_training.spring.events.ChatloopApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final List<SynchronizedSpace> stubbedSpaces;

    @SafeVarargs
    public StatefulCdnServiceStub(Transformer transformer, ChatloopApplicationEventPublisher applicationEventPublisher, Map<String, CDAEntry>... entryResponses) {
        super(transformer, applicationEventPublisher);
        this.stubbedSpaces = Arrays.stream(entryResponses).map(StubbedSynchronizedSpace::new).collect(Collectors.toList());
    }

    @Override
    protected void syncSchema() { }

    @Override
    protected SynchronizedSpace syncData() {
        var space = stubbedSpaces.remove(0);
        if (stubbedSpaces.isEmpty()) {
            stubbedSpaces.add(space);
        }
        return space;
    }

    @Override
    protected CDAClient createClient() {
        return null;
    }
}
