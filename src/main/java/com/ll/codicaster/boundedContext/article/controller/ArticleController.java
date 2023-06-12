package com.ll.codicaster.boundedContext.article.controller;

import java.util.List;

import com.ll.codicaster.base.rsData.RsData;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.base.rq.Rq;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.form.ArticleCreateForm;
import com.ll.codicaster.boundedContext.article.service.ArticleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/usr/article")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final Rq rq;


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String articleWrite() {
        return "usr/article/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String articleWriteSave(ArticleCreateForm articleCreateForm, @RequestParam("imageFile") MultipartFile imageFile) {
        RsData<Article> rsData = articleService.saveArticle(rq.getMember(), articleCreateForm, imageFile);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        return rq.redirectWithMsg("/usr/article/list", rsData);
    }

    @GetMapping("/list")
    public String articles(Model model) {

        List<Article> articles = articleService.articleList();
        model.addAttribute("articles", articles);

        return "usr/article/list";
    }

    //날짜 기준으로 정렬된 리스트 반환
    @GetMapping("/todayList")
    public String showArticlesFilteredByDate(Model model) {

        List<Article> articles = articleService.showArticlesFilteredByDate(rq.getMember());
        model.addAttribute("articlesFilteredOnce", articles);

        return "usr/article/nonmembers";
    }

    @GetMapping("/sortedlist")
    public String showArticlesFilteredByAllParams(Model model) {
        List<Article> filterdArticles = articleService.showArticlesFilteredByDate(rq.getMember());
        List<Article> articles = articleService.sortByAllParams(rq.getMember(), filterdArticles);
        model.addAttribute("articlesFilterdAndSorted", articles);


        return "usr/article/members";

    }

    @GetMapping("/detail/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {

        Article article = articleService.findById(id).orElse(null);
        if (article == null) {
            return rq.historyBack("해당 게시물이 존재하지 않습니다.");
        }

        model.addAttribute("article", article);
        model.addAttribute("likeCount", article.getLikesCount());
        if (rq.isLogout()) {
            return "usr/article/detail";
        }
        model.addAttribute("isLiked", article.isLiked(rq.getMember()));
        return "usr/article/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyArticle(@PathVariable("id") Long id, Model model) {
        if (rq.isLogout()) {
            return rq.historyBack("로그인이 필요한 기능입니다.");
        }
        Article article = articleService.findById(id).orElse(null);
        if (article == null) {
            return rq.historyBack("존재하지 않는 게시물입니다.");
        }
        if (article.getAuthor().getId() != rq.getMember().getId()) {
            return rq.historyBack("작성자만 수정 가능합니다.");
        }
        model.addAttribute("article", article);
        return "usr/article/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String updateArticle(@PathVariable("id") Long id, ArticleCreateForm updatedArticle, MultipartFile imageFile) {
        RsData<Article> rsData = articleService.updateArticle(rq.getMember(), id, updatedArticle, imageFile);

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        return rq.redirectWithMsg("/usr/article/detail/" + id, rsData);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable("id") Long id) {
        RsData rsData = articleService.deleteArticle(id, rq.getMember());
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        return rq.redirectWithMsg("/usr/article/list", rsData);
    }

    @RequestMapping("/myList")
    public String showMyArticle(Model model) {

        List<Article> articles = articleService.showMyList();
        model.addAttribute("myArticles", articles);

        List<String> mostUsedTags = rq.getMember().getMostUsedTags();
        model.addAttribute("mostUsedTags", mostUsedTags);


        return "usr/article/myList";
    }

    // 좋아요 추가
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/{id}")
    public String likeArticle(@PathVariable("id") Long id) {
        RsData rsData = articleService.likeArticle(rq.getMember(), id);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        return rq.redirectWithMsg("/usr/article/detail/" + id, rsData);
    }

    // 좋아요 취소
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/unlike/{id}")
    public String unlikeArticle(@PathVariable("id") Long id) {
        RsData rsData = articleService.unlikeArticle(rq.getMember(), id);
        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }
        return rq.redirectWithMsg("/usr/article/detail/" + id, rsData);
    }
}