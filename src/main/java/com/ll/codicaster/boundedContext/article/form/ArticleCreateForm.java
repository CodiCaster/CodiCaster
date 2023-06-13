package com.ll.codicaster.boundedContext.article.form;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ArticleCreateForm {
	private String title;
	private String content;
	private MultipartFile imageFile;

}