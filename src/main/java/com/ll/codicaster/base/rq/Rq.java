package com.ll.codicaster.base.rq;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.servlet.LocaleResolver;

import com.ll.codicaster.boundedContext.member.entity.Member;
import com.ll.codicaster.boundedContext.member.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
@RequestScope
public class Rq {
	private final MemberService memberService;
	private final MessageSource messageSource;
	private final LocaleResolver localeResolver;
	private Locale locale;
	private final HttpServletRequest req;
	private final HttpSession session;
	private final User user;
	private Member member = null; // 레이지 로딩, 처음부터 넣지 않고, 요청이 들어올 때 넣는다.

	public Rq(MemberService memberService, MessageSource messageSource, LocaleResolver localeResolver,
		HttpServletRequest req, HttpSession session) {
		this.memberService = memberService;
		this.messageSource = messageSource;
		this.localeResolver = localeResolver;
		this.req = req;
		this.session = session;
		// 현재 로그인한 회원의 인증정보를 가져옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication.getPrincipal() instanceof User) {
			this.user = (User)authentication.getPrincipal();
		} else {
			this.user = null;
		}
	}

	// 로그인 되어 있는지 체크
	public boolean isLogin() {
		return user != null;
	}

	// 로그아웃 되어 있는지 체크
	public boolean isLogout() {
		return !isLogin();
	}

	// 로그인 된 회원의 객체
	public Member getMember() {
		if (isLogout())
			return null;

		// 데이터가 없는지 체크
		if (member == null) {
			member = memberService.findByUsername(user.getUsername()).orElseThrow();
		}

		return member;
	}

	public String getCText(String code, String... args) {
		return messageSource.getMessage(code, args, getLocale());
	}

	private Locale getLocale() {
		if (locale == null)
			locale = localeResolver.resolveLocale(req);

		return locale;
	}

	public Long getLoginedMemberId() {
		return getMember().getId();
	}

	public boolean isRefererAdminPage() {
		SavedRequest savedRequest = (SavedRequest)session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");

		if (savedRequest == null)
			return false;

		String referer = savedRequest.getRedirectUrl();
		return referer != null && referer.contains("/adm");
	}
}
