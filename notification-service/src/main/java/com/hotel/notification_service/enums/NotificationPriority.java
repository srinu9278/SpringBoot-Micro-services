package com.hotel.notification_service.enums;

import lombok.Getter;

@Getter
public enum NotificationPriority {
    LOW("Low"),
    NORMAL("Normal"),
    HIGH("High"),
    URGENT("Urgent"),
    CRITICAL("Critical");

    private final String displayName;

    NotificationPriority(String displayName) {
        this.displayName = displayName;
    }
}

