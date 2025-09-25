package com.hotel.notification_service.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    EMAIL("Email"),
    SMS("SMS"),
    PUSH("Push Notification"),
    SLACK("Slack Message"),
    WEBHOOK("Webhook"),
    IN_APP("In-App Notification");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }
}

