package com.ll.codicaster.base.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterSaveWeather extends ApplicationEvent {
    private final Long weatherId;
    private final Long articleId;

    public EventAfterSaveWeather(Object source, Long weatherId, Long articleId) {
        super(source);
        this.weatherId = weatherId;
        this.articleId = articleId;
    }
}
