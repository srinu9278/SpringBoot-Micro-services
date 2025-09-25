package com.hotel.booking_service.enums;

public enum PaymentStatus {
    PENDING("Pending"),
    PAID("Paid"),
    PARTIALLY_PAID("Partially Paid"),
    REFUNDED("Refunded"),
    FAILED("Failed");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

