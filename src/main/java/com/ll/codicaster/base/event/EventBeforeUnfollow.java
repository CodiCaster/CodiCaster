package com.ll.codicaster.base.event;

import org.springframework.context.ApplicationEvent;

import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EventBeforeUnfollow extends ApplicationEvent {
	private final Long articleId;
	private Member followee;
	private Member follower;

	public EventBeforeUnfollow(Object source,Member follower ,Long articleId) {
		super(source);
		this.follower=follower;
		this.articleId=articleId;
	}
}
