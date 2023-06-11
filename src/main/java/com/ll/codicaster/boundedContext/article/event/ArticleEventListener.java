package com.ll.codicaster.boundedContext.article.event;

import com.ll.codicaster.boundedContext.article.entity.Article;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ll.codicaster.base.event.EventBeforeFollow;
import com.ll.codicaster.boundedContext.article.service.ArticleService;
import com.ll.codicaster.boundedContext.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional
public class ArticleEventListener {
    private final ArticleService articleService;

    @EventListener
    @Order(1)
    public void listen(EventBeforeFollow event) {
        Article article = articleService.findById(event.getArticleId()).orElse(null);
        if (article != null) {
            event.setFollowee(article.getAuthor());
        }
    }
}

