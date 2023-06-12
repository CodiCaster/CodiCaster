package com.ll.codicaster.boundedContext.follow.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.base.rsData.RsData;
import com.ll.codicaster.boundedContext.follow.service.FollowService;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usr")
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;
	private final ApplicationEventPublisher publisher;
	private final Rq rq;

	@PostMapping("/follow/{id}")
	public String followMember(
		@PathVariable("id") Long articleId) {
		Member follower = rq.getMember();
		RsData followResult = followService.whenBeforeFollow(follower, articleId);

		if (followResult.isFail())
			return rq.historyBack(followResult.getMsg());

		// 게시물 상세페이지로 리다이렉트
		return "redirect:/usr/article/detail/" + articleId;
	}
	@PostMapping("/unfollow/{id}")
	public String unfollowMember(
		@PathVariable("id") Long articleId) {
		Member follower = rq.getMember();
		RsData followResult = followService.whenBeforeUnfollow(follower, articleId);

		if (followResult.isFail())
			return rq.historyBack(followResult.getMsg());

		// 게시물 상세페이지로 리다이렉트
		return "redirect:/usr/article/detail/" + articleId;
	}
}
