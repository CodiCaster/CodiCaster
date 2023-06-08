package com.ll.codicaster.base.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterDelete extends ApplicationEvent {
    private final Long articleId;

    public EventAfterDelete(Object source, Long articleId) {
        super(source);
        this.articleId = articleId;
    }
}
