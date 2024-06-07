package com.huy.airbnbserver.notification;

import com.huy.airbnbserver.notification.model.Notification;
import com.huy.airbnbserver.system.common.Result;
import com.huy.airbnbserver.system.event.ui.SendingNotificationEvent;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDate;
import java.util.Date;

@RestController
@RequestMapping("/api/v1")
public class NotificationController {
    private final Sinks.Many<NotificationDto> notificationEventSink;
    private final NotificationService notificationService;
    private final NotificationDtoConverter converter;

    public NotificationController(NotificationService notificationService, NotificationDtoConverter converter) {
        this.notificationEventSink = Sinks.many().multicast().onBackpressureBuffer();
        this.notificationService = notificationService;
        this.converter = converter;
    }

    @GetMapping(value = "/notifications/subscribe", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<NotificationDto> streamNotifications(@RequestParam Integer userId) {
        return notificationEventSink.asFlux()
                .filter(notification -> notification.receiver_id().equals(userId));
    }

    @GetMapping(value = "/notifications/{userId}")
    public Result getAllMessage(@PathVariable Integer userId) {
        return new Result(
                true,
                200,
                "Fetching All Notification",
                notificationService.findAll(userId).stream().map(converter::convert).toList()
        );

    }

    @GetMapping(value = "/notifications/test")
    public void test() {
         sendNotification(
                new SendingNotificationEvent(2,502L,"test"));
    }

    public void sendNotification(SendingNotificationEvent event) {
//        notificationService.save(event);
        notificationEventSink.tryEmitNext(
                new NotificationDto(
                        event.getReceiverID(),
                        event.getMessage(),
                        false,
                        new Date()
                )
        );
    }
}
