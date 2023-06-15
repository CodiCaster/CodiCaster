package com.ll.codicaster.boundedContext.article.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ArticleDTO {
	private String title;
	private String content;
	private MultipartFile imageFile;

}