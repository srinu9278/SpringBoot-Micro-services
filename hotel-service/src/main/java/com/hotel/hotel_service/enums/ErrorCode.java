package com.hotel.hotel_service.enums;

public enum ErrorCode {
    // Hotel related errors
    HOTEL_ALREADY_EXISTS("HOTEL_001", "Hotel already exists with the given name and address"),
    HOTEL_NOT_FOUND("HOTEL_002", "Hotel not found"),
    INVALID_HOTEL_DATA("HOTEL_003", "Invalid hotel data provided"),
    INVALID_HOTEL_STATUS("HOTEL_004", "Invalid hotel status provided"),
    HOTEL_DATA_NOT_FOUND("HOTEL_005", "Hotel data not found"),
    
    // Validation errors
    VALIDATION_ERROR("VAL_001", "Validation error"),
    REQUIRED_FIELD_MISSING("VAL_002", "Required field is missing"),
    INVALID_FORMAT("VAL_003", "Invalid format"),
    
    // Database errors
    DATABASE_ERROR("DB_001", "Database operation failed"),
    CONNECTION_ERROR("DB_002", "Database connection error"),
    
    // General errors
    INTERNAL_SERVER_ERROR("GEN_001", "Internal server error"),
    UNAUTHORIZED("GEN_002", "Unauthorized access"),
    FORBIDDEN("GEN_003", "Access forbidden"),
    RESOURCE_NOT_FOUND("GEN_004", "Resource not found");

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
