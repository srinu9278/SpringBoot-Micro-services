package com.hotel.notification_service.enums;

import lombok.Getter;

@Getter
public enum NotificationStatus {
    PENDING("Pending"),
    SENT("Sent"),
    DELIVERED("Delivered"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    RETRYING("Retrying");

    private final String displayName;

    NotificationStatus(String displayName) {
        this.displayName = displayName;
    }
}


