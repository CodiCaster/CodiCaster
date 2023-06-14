package com.ll.codicaster.base.event;

import org.springframework.context.ApplicationEvent;

import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.Getter;

@Getter
public class EventBeforeDeleteArticle extends ApplicationEvent {

	private final Article article;

	public EventBeforeDeleteArticle(Object source, Article article) {
		super(source);
		this.article = article;
	}
}
