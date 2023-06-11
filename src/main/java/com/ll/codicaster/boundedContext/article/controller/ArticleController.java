package com.ll.codicaster.boundedContext.article.controller;

import java.util.List;

import com.ll.codicaster.boundedContext.member.service.MemberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.form.ArticleCreateForm;
import com.ll.codicaster.boundedContext.article.service.ArticleService;
import com.ll.codicaster.boundedContext.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usr/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final MemberService memberService;
    private final Rq rq;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String articleWrite() {
//        날짜 선택 기능 - 고도화 작업에 해당
//        LocalDate currentDate = LocalDate.now();
//        model.addAttribute("currentDate", currentDate);
        return "usr/article/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/writepro")
    public String articleWriteSave(@ModelAttribute ArticleCreateForm articleCreateForm,
                                   @RequestParam("imageFile") MultipartFile imageFile) {

        articleService.saveArticle(rq.getMember(), articleCreateForm, imageFile);

        return "redirect:/usr/article/list";
    }

    @GetMapping("/list")
    public String articles(Model model) {

        List<Article> articles = articleService.articleList();
        model.addAttribute("articles", articles);

        return "usr/article/list";
    }

    @GetMapping("/todayList")
    public String showArticlesNearbyToday(Model model) {

        List<Article> articles = articleService.showArticlesNearbyToday(rq.getMember());
        model.addAttribute("articlesNearbyToday", articles);

        return "usr/article/todayList";
    }

    @GetMapping("/detail/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {

        Article article = articleService.articleDetail(id);
        Member currentMember = rq.getMember();  // 현재 사용자 가져오기
        boolean isLiked = article.getLikedMembers().contains(currentMember);  // 현재 사용자가 이 게시글에 좋아요를 눌렀는지 판단

        model.addAttribute("article", article);
        model.addAttribute("image", article.getImage());
        model.addAttribute("likeCount", article.getLikesCount());
        model.addAttribute("isLiked", isLiked);
        model.addAttribute("address", article.getAddress());
        model.addAttribute("weatherInfo", article.getWeatherInfo());

        return "usr/article/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyArticle(@PathVariable("id") Long id, Model model) {

        Article article = articleService.findArticleById(id);

        if (article == null) {
            return "redirect:/error";
        }

        model.addAttribute("article", article);

        return "usr/article/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String updateArticle(@PathVariable("id") Long id, @ModelAttribute ArticleCreateForm updatedArticle,
                                MultipartFile imageFile) {
        boolean success = articleService.updateArticle(rq.getMember(), id, updatedArticle, imageFile);

        if (!success) {
            return "redirect:/error";
        }

        return "redirect:/usr/article/detail/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable("id") Long id) {

        boolean success = articleService.deleteArticle(id);

        if (!success) {
            return "redirect:/error";
        }

        return "redirect:/usr/article/list";
    }

    @RequestMapping("/myList")
    public String showMyArticle(Model model) {

        List<Article> articles = articleService.showMyList();
        model.addAttribute("myArticles", articles);

        List<String> mostUsedTags = memberService.getMostUsedTags();
        model.addAttribute("mostUsedTags", mostUsedTags);


        return "usr/article/myList";
    }

    // 좋아요 추가
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/{id}")
    public String likeArticle(@PathVariable("id") Long id) {
        boolean success = articleService.likeArticle(rq.getMember(), id);

        if (!success) {
            return "redirect:/error";
        }

        return "redirect:/usr/article/detail/" + id;
    }

    // 좋아요 취소
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unlike/{id}")
    public String unlikeArticle(@PathVariable("id") Long id) {
        boolean success = articleService.unlikeArticle(rq.getMember(), id);

        if (!success) {
            return "redirect:/error";
        }
        return "redirect:/usr/article/detail/" + id;
    }
}