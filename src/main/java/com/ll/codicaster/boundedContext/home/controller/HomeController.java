package com.ll.codicaster.boundedContext.home.controller;

import java.util.Enumeration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ll.codicaster.base.rq.Rq;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final Rq rq;

	@GetMapping("/")
	public String showMain(HttpSession session) {
		// 로그인 상태일 경우 /usr/member/me 페이지로 이동
		if (rq.isLogin()) {
			String nickname = rq.getNickname();

			// 이미 가입한 회원인 경우 /usr/member/me 페이지로 이동
			if (nickname != null) {
				return "redirect:/usr/member/me";
			}
			else {
				return "redirect:/usr/member/newInfo";
			}
		}

		// 로그아웃 상태일 경우 로그인 페이지로 리디렉션
		if (rq.isLogout()) {
			return "redirect:/usr/member/login";
		}

		// 회원가입 폼으로 리디렉션
		return "redirect:/usr/member/newInfo";
	}

	@GetMapping("/usr/home/about")
	public String showAbout() {
		return "usr/home/about";
	}

	@GetMapping("/usr/debugSession")
	@ResponseBody
	public String showDebugSession(HttpSession session) {
		StringBuilder sb = new StringBuilder("Session content:\n");

		Enumeration<String> attributeNames = session.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			Object attributeValue = session.getAttribute(attributeName);
			sb.append(String.format("%s: %s\n", attributeName, attributeValue));
		}

		return sb.toString().replaceAll("\n", "<br>");
	}

}
