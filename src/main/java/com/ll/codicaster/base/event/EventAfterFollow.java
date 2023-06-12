package com.ll.codicaster.base.event;

import org.springframework.context.ApplicationEvent;

import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.Getter;

@Getter
public class EventAfterFollow extends ApplicationEvent {
	private final Member follower;
	private final Member followee;

	public EventAfterFollow(Object source, Member follower, Member followee) {
		super(source);
		this.follower = follower;
		this.followee = followee;

	}
}
