package com.ll.codicaster.boundedContext.home.controller;

import java.util.Enumeration;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.service.ArticleService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final Rq rq;
	private final ArticleService articleService;

	@GetMapping("/main")
	public String showMain(Model model) {
		List<Article> nonmemberArticles = articleService.showArticlesFilteredByDate();
		model.addAttribute("articlesFilteredOnce", nonmemberArticles);

		if (rq.isLogin()) {
			List<Article> memberArticles = articleService.sortByAllParams(rq.getMember(), nonmemberArticles);
			model.addAttribute("articlesFilterdAndSorted", memberArticles);
			List<Article> myArticles = articleService.showMyList();
			model.addAttribute("myArticles", myArticles);
		}
		return "usr/home/main";
	}

	@GetMapping("/")
	public String redirectToMain() {
		if (rq.isLogout()) {
			return "redirect:/main";
		}
		if (rq.getNickname() == null) {
			return "redirect:/usr/member/newInfo";
		}
		return "redirect:/main";
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
