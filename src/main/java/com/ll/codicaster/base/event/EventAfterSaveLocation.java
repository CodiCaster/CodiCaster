package com.ll.codicaster.base.event;

import com.ll.codicaster.boundedContext.location.entity.Location;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventAfterSaveLocation extends ApplicationEvent {
    private final Long locationId;
    private final Long articleId;

    public EventAfterSaveLocation(Object source, Long locationId, Long articleId) {
        super(source);
        this.locationId = locationId;
        this.articleId = articleId;
    }
}
