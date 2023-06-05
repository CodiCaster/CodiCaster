package com.ll.codicaster.boundedContext.article.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final Rq rq;


    @GetMapping("/write")
    public String articleWrite() {
        return "usr/article/write";
    }


    @PostMapping("/writepro")
    public String articleWriteSave(@ModelAttribute ArticleCreateForm articleCreateForm, @RequestParam("imageFile") MultipartFile imageFile) throws Exception {

        articleService.saveArticle(rq.getMember(), articleCreateForm, imageFile);

        return "redirect:/usr/article/list";
    }



    @GetMapping("/list")
    public String articles(Model model) {

        List<Article> articles = articleService.articleList();
        model.addAttribute("articles", articles);

        return "usr/article/list";
    }

    @GetMapping("/list/nearby")
    public String showArticlesNearbyToday(Model model) {

        List<Article> articles = articleService.showArticlesNearbyToday();
        model.addAttribute("articlesNearbyToday", articles);

        return "usr/article/todaylist";
    }

    @GetMapping("/detail/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {
        Article article = articleService.articleDetail(id);
        model.addAttribute("article", article);
        model.addAttribute("image", article.getImage());
        return "usr/article/detail";
    }

    @GetMapping("/modify/{id}")
    public String modifyArticle(@PathVariable("id") Long id, Model model) {

        Article article = articleService.findArticleById(id);

        if (article == null) {
            return "redirect:/error";
        }

        model.addAttribute("article", article);

        return "usr/article/modify";
    }

    @PostMapping("/modify/{id}")
    public String updateArticle(@PathVariable("id") Long id, @ModelAttribute ArticleCreateForm updatedArticle, MultipartFile imageFile) {
        boolean success = articleService.updateArticle(id, updatedArticle, imageFile);

        if (!success) {
            return "redirect:/error";
        }

        return "redirect:/usr/article/detail/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable("id") Long id) {

        boolean success = articleService.deleteArticle(id);

        if (!success) {
            return "redirect:/error";
        }


        return "redirect:/usr/article/list";
    }


}


