package com.huy.airbnbserver.system.event.ui;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class SendingNotificationEvent {
    private Integer receiverID;
    private Long referencesObjectID;
    private String message;
}
