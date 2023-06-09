package com.ll.codicaster.boundedContext.article.form;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class ArticleCreateForm {
	private String title;
	//날짜 선택 기능 구현시 사용
//	private LocalDate customDate;
	private String content;
	private MultipartFile imageFile;

}