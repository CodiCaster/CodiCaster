package com.ll.codicaster.boundedContext.article.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.form.ArticleCreateForm;
import com.ll.codicaster.boundedContext.article.service.ArticleService;
import com.ll.codicaster.boundedContext.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

	private final ArticleService articleService;

	@GetMapping("/write")
	public String articleWrite() {
		return "usr/article/write";
	}


	@PostMapping("/writepro")
	public String articleWriteSave(@ModelAttribute ArticleCreateForm articleCreateForm, @RequestParam("imageFile") MultipartFile imageFile) throws Exception {
		articleService.saveArticle(articleCreateForm, imageFile);

		return "redirect:/article/list";
	}


}


