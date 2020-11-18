package org.chatloop.user_training.model.progress;

import lombok.*;

import java.util.List;

@Data
public final class QualificationChangeSummary {
    private final List<String> usersToReset;
    private final List<EarnedUserQualificationTo> changes;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EarnedUserQualificationTo {
        private String userId;
        private List<String> qualifications;
    }
}
