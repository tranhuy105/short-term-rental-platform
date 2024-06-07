package com.huy.airbnbserver.notification;

import java.util.Date;

public record NotificationDto(
        Integer receiver_id,
        String message,
        Boolean is_read,
        Date created_at
) {
}
