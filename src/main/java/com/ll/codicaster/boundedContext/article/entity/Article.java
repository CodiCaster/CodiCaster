package com.ll.codicaster.boundedContext.article.entity;

import java.time.LocalDateTime;

import com.ll.codicaster.boundedContext.image.entity.Image;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Article {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String content;

	private String author;
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	private Integer likeCount;

	@OneToOne(mappedBy = "article", cascade = CascadeType.ALL)
	private Image image;


	//ManyToMany 태그리스트와 연결
	// @ElementCollection
	// private List<String> tags;

}
