package com.hotel.room_service.enums;

public enum RoomStatus {
    AVAILABLE("Available"),
    OCCUPIED("Occupied"),
    OUT_OF_ORDER("Out of Order"),
    MAINTENANCE("Maintenance"),
    CLEANING("Cleaning"),
    RESERVED("Reserved");
    
    private final String displayName;
    
    RoomStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
