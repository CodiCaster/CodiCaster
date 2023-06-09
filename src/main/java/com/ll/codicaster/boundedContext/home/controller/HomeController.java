package com.ll.codicaster.boundedContext.home.controller;

import java.util.Enumeration;
import java.util.List;

import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ll.codicaster.base.rq.Rq;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ArticleService articleService;
    private final Rq rq;

    @GetMapping("/usr/main")
    public String mainTestLogin(Model model) {

        List<Article> articles = articleService.showArticlesNearbyToday(rq.getMember());
        model.addAttribute("articlesNearbyToday", articles);

        return "/usr/home/main";
    }

    @GetMapping("/")
    public String showMain() {
        if (rq.isLogout()) {
            return "redirect:/usr/member/login";
        }

        // if (rq.getMember().getNickname() != null || rq.getMember().getBodytype() != null)
        // 	return "usr/home/about";

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
