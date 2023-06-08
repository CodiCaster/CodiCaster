package com.ll.codicaster.boundedContext.article.form;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class ArticleCreateForm {
	private String title;
	private LocalDate customDate;
	private String content;
	private MultipartFile imageFile;

}