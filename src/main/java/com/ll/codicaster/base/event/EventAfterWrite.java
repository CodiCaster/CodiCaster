package com.ll.codicaster.base.event;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.location.entity.Location;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterWrite extends ApplicationEvent {
    private final Rq rq;
    private final Long articleId;

    public EventAfterWrite(Object source, Rq rq, Long articleId) {
        super(source);
        this.rq = rq;
        this.articleId = articleId;
    }
}
