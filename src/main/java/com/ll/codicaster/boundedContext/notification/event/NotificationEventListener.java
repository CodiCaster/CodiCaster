package com.ll.codicaster.boundedContext.notification.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.base.event.EventAfterFollow;
import com.ll.codicaster.base.event.EventAfterLike;
import com.ll.codicaster.base.event.EventBeforeDeleteArticle;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.notification.service.NotificationService;

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
		String typeCode = "LIKE";
		notificationService.makeLike(article,typeCode,liker);
	}

	@EventListener
	public void listen(EventAfterFollow event) {
		Member follower = event.getFollower();
		Member followee = event.getFollowee();
		String typeCode = "FOLLOW";
		notificationService.makeFollow(follower,followee,typeCode);
	}

	@EventListener
	public void listen(EventBeforeDeleteArticle event){

		Article article = event.getArticle();
		notificationService.delete(article);
	}
}
