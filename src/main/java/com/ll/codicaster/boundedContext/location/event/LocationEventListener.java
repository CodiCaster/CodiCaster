package com.ll.codicaster.boundedContext.location.event;

import com.ll.codicaster.base.event.EventAfterWrite;
import com.ll.codicaster.boundedContext.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class LocationEventListener {
    private final LocationService locationService;

    @EventListener
    public void listen(EventAfterWrite event) {
        locationService.whenAfterWrite(event.getRq(), event.getArticle());
    }
}
