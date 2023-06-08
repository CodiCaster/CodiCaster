package com.ll.codicaster.boundedContext.weather.event;

import com.ll.codicaster.base.event.EventAfterWrite;
import com.ll.codicaster.boundedContext.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class WeatherEventListener {
    private final WeatherService weatherService;

    @EventListener
    public void save(EventAfterWrite event) {
        weatherService.whenAfterWrite(event.getRq(), event.getArticleId());
    }
}
