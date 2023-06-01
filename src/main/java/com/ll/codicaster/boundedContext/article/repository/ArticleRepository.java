package com.ll.codicaster.boundedContext.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ll.codicaster.boundedContext.article.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
}
