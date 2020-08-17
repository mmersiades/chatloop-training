package com.chatloop.training.model.content;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Setter
public class Task extends IdAware {

    private String name;
}