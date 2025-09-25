package com.hotel.room_service.enums;

public enum ErrorCode {
    
    ROOM_ALREADY_EXISTS("ROOM_001", "Room already exists with this room number in the hotel"),
    ROOM_NOT_FOUND("ROOM_002", "Room not found"),
    INVALID_ROOM_DATA("ROOM_003", "Invalid room data provided"),
    INVALID_ROOM_STATUS("ROOM_004", "Invalid room status"),
    ROOM_DATA_NOT_FOUND("ROOM_005", "Room data not found"),
    VALIDATION_ERROR("ROOM_006", "Validation error"),
    DATABASE_ERROR("ROOM_007", "Database error"),
    INTERNAL_SERVER_ERROR("ROOM_008", "Internal server error"),
    ROOM_NUMBER_ALREADY_EXISTS("ROOM_009", "Room number already exists in this hotel"),
    INVALID_ROOM_TYPE("ROOM_010", "Invalid room type"),
    INVALID_PRICE_RANGE("ROOM_011", "Invalid price range"),
    ROOM_NOT_AVAILABLE("ROOM_012", "Room is not available"),
    INVALID_OCCUPANCY("ROOM_013", "Invalid occupancy requirements"),
    ROOM_UPDATE_FAILED("ROOM_014", "Room update failed"),
    ROOM_DELETE_FAILED("ROOM_015", "Room deletion failed"),
    HOTEL_NOT_FOUND("HOTEL_001","Hotel not found");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
