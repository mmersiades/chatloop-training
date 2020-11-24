package org.chatloop.user_training.model.content;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@ToString
public class EarnedQualification extends IdAware {
    @Setter
    private String name;
}
