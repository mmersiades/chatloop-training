package org.chatloop.user_training.spring.events;

import lombok.extern.slf4j.Slf4j;
import org.chatloop.user_training.model.progress.EarnedQualifications;
import org.chatloop.user_training.service.EarnedQualificationsService;
import org.chatloop.user_training.service.QualificationEvaluationService;
import org.springframework.context.ApplicationListener;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
public class ContentUpdatedEventListener implements ApplicationListener<ContentUpdatedEvent> {

    private final QualificationEvaluationService evaluationService;
    private final EarnedQualificationsService earnedQualificationsService;

    public ContentUpdatedEventListener(QualificationEvaluationService evaluationService, EarnedQualificationsService earnedQualificationsService) {
        this.evaluationService = evaluationService;
        this.earnedQualificationsService = earnedQualificationsService;
    }

    @Override
    public void onApplicationEvent(ContentUpdatedEvent contentUpdatedEvent) {
        log.debug("Received {}", contentUpdatedEvent.getClass().getSimpleName());

        var toRemove = new ArrayList<EarnedQualifications>();
        var changes = new ArrayList<EarnedQualifications>();

        var existingQualifications = earnedQualificationsService
                .fetchAllEarnedQualifications()
                .collect(Collectors.toMap(EarnedQualifications::getUserId, e -> e));

        var evaluatedQualifications = evaluationService
                .evaluateAllUsers()
                .asEarnedQualifications()
                .collect(Collectors.toMap(EarnedQualifications::getUserId, e -> e));

        // find users who lost all their earned qualifications
        for (String userId : existingQualifications.keySet()) {
            if (!evaluatedQualifications.containsKey(userId)) {
                toRemove.add(existingQualifications.get(userId));
            }
        }

        // find users whose qualifications have changed
        for (String userId : evaluatedQualifications.keySet()) {
            var now = evaluatedQualifications.get(userId);
            var before = existingQualifications.get(userId);
            // if the user didn't have any previous qualifications or they were different
            if (before == null || !now.getQualifications().containsAll(before.getQualifications()) || !before.getQualifications().containsAll(now.getQualifications())) {
                changes.add(now);
            }
        }

        // handle removals
        earnedQualificationsService.removeEarnedQualifications(toRemove.toArray(new EarnedQualifications[0]));

        // handle changes
        earnedQualificationsService.storeEarnedQualifications(changes.toArray(new EarnedQualifications[0]));

        // notify downstream
    }
}
