package com.ll.codicaster.boundedContext.notification;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.base.event.EventAfterLike;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationEventListener {
	private final NotificationService notificationService;

	@EventListener
	public void listen(EventAfterLike event) {
		Article article = event.getArticle();
		Member liker = event.getLiker();
		notificationService.makeLike(article,liker);
	}
}
