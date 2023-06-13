package com.ll.codicaster.boundedContext.article.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ll.codicaster.boundedContext.article.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
	List<Article> findByCreateDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

	List<Article> findByAuthorId(Long authorId);
}
