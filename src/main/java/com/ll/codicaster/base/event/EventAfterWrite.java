package com.ll.codicaster.base.event;

import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.location.entity.Location;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterWrite extends ApplicationEvent {
    private final Location location;
    private final Article article;

    public EventAfterWrite(Object source, Location location, Article article) {
        super(source);
        this.location = location;
        this.article = article;
    }
}
