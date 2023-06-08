package com.ll.codicaster.boundedContext.article.event;

import com.ll.codicaster.base.event.EventAfterSaveLocation;
import com.ll.codicaster.base.event.EventAfterSaveWeather;
import com.ll.codicaster.base.event.EventAfterWrite;
import com.ll.codicaster.boundedContext.article.entity.Article;
import com.ll.codicaster.boundedContext.article.service.ArticleService;
import com.ll.codicaster.boundedContext.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ArticleEventListener {
    private final ArticleService articleService;

    @EventListener
    public void listen(EventAfterSaveLocation event) {
        articleService.whenAfterSaveLocation(event.getLocationId(), event.getArticleId());
    }

    @EventListener
    public void listen(EventAfterSaveWeather event) {
        articleService.whenAfterSaveWeather(event.getWeatherId(), event.getArticleId());
    }
}
