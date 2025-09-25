package com.hotel.room_service.enums;

public enum RoomType {
    SINGLE("Single"),
    DOUBLE("Double"),
    TWIN("Twin"),
    TRIPLE("Triple"),
    QUAD("Quad"),
    SUITE("Suite"),
    DELUXE("Deluxe"),
    PRESIDENTIAL("Presidential"),
    FAMILY("Family"),
    STUDIO("Studio");
    
    private final String displayName;
    
    RoomType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
