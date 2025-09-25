package com.hotel.payment_service.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "Payment not found"),
    PAYMENT_ALREADY_EXISTS("PAYMENT_ALREADY_EXISTS", "Payment already exists"),
    INVALID_PAYMENT_DATA("INVALID_PAYMENT_DATA", "Invalid payment data provided"),
    INVALID_PAYMENT_STATUS("INVALID_PAYMENT_STATUS", "Invalid payment status"),
    PAYMENT_PROCESSING_FAILED("PAYMENT_PROCESSING_FAILED", "Payment processing failed"),
    REFUND_NOT_ALLOWED("REFUND_NOT_ALLOWED", "Refund not allowed for this payment"),
    REFUND_AMOUNT_EXCEEDED("REFUND_AMOUNT_EXCEEDED", "Refund amount exceeds payment amount"),
    PAYMENT_EXPIRED("PAYMENT_EXPIRED", "Payment has expired"),
    GATEWAY_ERROR("GATEWAY_ERROR", "Payment gateway error"),
    INSUFFICIENT_FUNDS("INSUFFICIENT_FUNDS", "Insufficient funds"),
    CARD_DECLINED("CARD_DECLINED", "Card declined"),
    INVALID_CARD("INVALID_CARD", "Invalid card details"),
    DUPLICATE_TRANSACTION("DUPLICATE_TRANSACTION", "Duplicate transaction detected"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation error"),
    DATABASE_ERROR("DATABASE_ERROR", "Database error"),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "Internal server error"),
    HOTELID_NOT_FOUND("HOTELID_NOT_FOUND","HotelId not found");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}


