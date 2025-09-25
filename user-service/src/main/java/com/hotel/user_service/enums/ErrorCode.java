package com.hotel.user_service.enums;

public enum ErrorCode {
    // User related errors
    USER_ALREADY_EXISTS("USER_001", "User already exists with the given username"),
    USER_NOT_FOUND("USER_002", "User not found"),
    INVALID_USER_DATA("USER_003", "Invalid user data provided"),
    INVALID_ROLE("USER_004", "Invalid role provided"),
    DATA_NOT_FOUND("USER_005","Data Not Found"),
    
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