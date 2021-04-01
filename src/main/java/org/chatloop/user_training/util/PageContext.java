package org.chatloop.user_training.util;

import lombok.Getter;
import lombok.Setter;
import org.chatloop.user_training.model.progress.CourseProgress;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Getter
@Setter
public final class PageContext {
    private final String contextName;
    private Slice<CourseProgress> slice;
    private Pageable pageable;

    public PageContext(String contextName, int pageSize) {
        this.contextName = contextName;
        this.pageable = PageRequest.of(0, pageSize);
    }
}
