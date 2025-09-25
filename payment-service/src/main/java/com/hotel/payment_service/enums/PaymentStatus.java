package com.hotel.payment_service.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    REFUNDED("Refunded"),
    PARTIALLY_REFUNDED("Partially Refunded"),
    EXPIRED("Expired");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
}

