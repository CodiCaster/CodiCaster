package com.ll.codicaster.boundedContext.follow.event;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.base.event.EventBeforeFollow;
import com.ll.codicaster.base.event.EventBeforeUnfollow;
import com.ll.codicaster.boundedContext.follow.service.FollowService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class FollowEventListener {
	private final FollowService followService;

	@EventListener
	@Order(2)
	public void listen(EventBeforeFollow event) {
		followService.followMember(event.getFollowee(), event.getFollower());
	}

	@EventListener
	@Order(2)
	public void listen(EventBeforeUnfollow event) {
		followService.unfollowMember(event.getFollowee(), event.getFollower());
	}
}
