package com.ll.codicaster.base.event;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.article.entity.Article;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterWrite extends ApplicationEvent {
    private final Rq rq;
    private final Article article;

    public EventAfterWrite(Object source, Rq rq, Article article) {
        super(source);
        this.rq = rq;
        this.article = article;
    }
}
