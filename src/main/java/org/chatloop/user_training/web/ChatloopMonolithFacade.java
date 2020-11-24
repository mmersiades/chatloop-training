package org.chatloop.user_training.web;

import org.chatloop.user_training.model.progress.QualificationChangeSummary;

/**
 * Provide access to the Chatloop monolith.
 */
public interface ChatloopMonolithFacade {
    /**
     * Subclasses can implement this to notify about changes to the earned qualifications.
     * @param summary {@link QualificationChangeSummary}
     */
    void notifyEarnedQualificationsChanges(QualificationChangeSummary summary);
}
