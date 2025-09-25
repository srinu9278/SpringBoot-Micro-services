package com.hotel.notification_service.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "Notification not found"),
    NOTIFICATION_ALREADY_EXISTS("NOTIFICATION_ALREADY_EXISTS", "Notification already exists"),
    INVALID_NOTIFICATION_DATA("INVALID_NOTIFICATION_DATA", "Invalid notification data provided"),
    INVALID_NOTIFICATION_STATUS("INVALID_NOTIFICATION_STATUS", "Invalid notification status"),
    NOTIFICATION_SENDING_FAILED("NOTIFICATION_SENDING_FAILED", "Failed to send notification"),
    SLACK_CONNECTION_FAILED("SLACK_CONNECTION_FAILED", "Slack connection failed"),
    EMAIL_SENDING_FAILED("EMAIL_SENDING_FAILED", "Email sending failed"),
    SMS_SENDING_FAILED("SMS_SENDING_FAILED", "SMS sending failed"),
    TEMPLATE_NOT_FOUND("TEMPLATE_NOT_FOUND", "Notification template not found"),
    INVALID_TEMPLATE_DATA("INVALID_TEMPLATE_DATA", "Invalid template data"),
    NOTIFICATION_EXPIRED("NOTIFICATION_EXPIRED", "Notification has expired"),
    MAX_RETRIES_EXCEEDED("MAX_RETRIES_EXCEEDED", "Maximum retry attempts exceeded"),
    INVALID_RECIPIENT("INVALID_RECIPIENT", "Invalid recipient format"),
    CHANNEL_NOT_FOUND("CHANNEL_NOT_FOUND", "Notification channel not found"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation error"),
    DATABASE_ERROR("DATABASE_ERROR", "Database error"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal server error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

