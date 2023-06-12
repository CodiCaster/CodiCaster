package com.ll.codicaster.base.event;

import org.springframework.context.ApplicationEvent;

import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.Getter;

@Getter
public class EventAfterLike extends ApplicationEvent {
    private final Member liker;
    private final Article article;

    public EventAfterLike(Object source, Member liker, Article article) {
        super(source);
        this.liker = liker;
        this.article = article;
    }
}
