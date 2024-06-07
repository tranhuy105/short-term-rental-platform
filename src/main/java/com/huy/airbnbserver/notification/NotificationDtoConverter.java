package com.huy.airbnbserver.notification;

import com.huy.airbnbserver.notification.model.Notification;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class NotificationDtoConverter implements Converter<Notification, NotificationDto> {
    @Override
    public NotificationDto convert(Notification source) {
        return new NotificationDto(
                source.getUser().getId(),
                source.getMessage(),
                source.getIsRead(),
                source.getCreatedAt()
        );
    }
}
