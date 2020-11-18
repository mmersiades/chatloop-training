package org.chatloop.user_training.web.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.chatloop.user_training.model.progress.QualificationChangeSummary;
import org.chatloop.user_training.model.progress.QualificationChangeSummary.EarnedUserQualificationTo;
import org.chatloop.user_training.util.enums.Qualifications;
import org.chatloop.user_training.web.ChatloopMonolithFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatloopMonolithFacadeImplTest {
    @DisplayName("should notify about qualification changes in chunks")
    @Test
    public void testNotifyEarnedQualificationsChanges() {
        var removals = IntStream.range(0, 53)
                .mapToObj(i -> RandomStringUtils.randomAlphabetic(10))
                .collect(Collectors.toList());

        var changes = IntStream.range(0, 833)
                .mapToObj(i -> new EarnedUserQualificationTo(RandomStringUtils.randomAlphabetic(10), Collections.singletonList(Qualifications.TOPIC_TEXTS.name())))
                .collect(Collectors.toList());

        QualificationChangeSummary summary = new QualificationChangeSummary(removals, changes);

        var summaries = new ArrayList<QualificationChangeSummary>();
        ChatloopMonolithFacade facade = new ChatloopMonolithFacadeImpl(() -> "") {
            @Override
            protected Integer sendChangeSummaryRequest(QualificationChangeSummary summary) {
                summaries.add(summary);
                return summary.getChanges().size() + summary.getUsersToReset().size();
            }
        };
        facade.notifyEarnedQualificationsChanges(summary);

        assertEquals(5, summaries.size());
    }
}