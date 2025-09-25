package com.hotel.booking_service.enums;

public enum ErrorCode {
    
    BOOKING_ALREADY_EXISTS("BOOKING_001", "Booking already exists with this reference"),
    BOOKING_NOT_FOUND("BOOKING_002", "Booking not found"),
    INVALID_BOOKING_DATA("BOOKING_003", "Invalid booking data provided"),
    INVALID_BOOKING_STATUS("BOOKING_004", "Invalid booking status"),
    INVALID_PAYMENT_STATUS("BOOKING_005", "Invalid payment status"),
    BOOKING_DATA_NOT_FOUND("BOOKING_006", "Booking data not found"),
    VALIDATION_ERROR("BOOKING_007", "Validation error"),
    DATABASE_ERROR("BOOKING_008", "Database error"),
    INTERNAL_SERVER_ERROR("BOOKING_009", "Internal server error"),
    BOOKING_REFERENCE_ALREADY_EXISTS("BOOKING_010", "Booking reference already exists"),
    INVALID_DATE_RANGE("BOOKING_011", "Invalid date range"),
    ROOM_NOT_AVAILABLE("BOOKING_012", "Room is not available for the selected dates"),
    BOOKING_CANNOT_BE_CANCELLED("BOOKING_013", "Booking cannot be cancelled"),
    BOOKING_CANNOT_BE_MODIFIED("BOOKING_014", "Booking cannot be modified"),
    BOOKING_UPDATE_FAILED("BOOKING_015", "Booking update failed"),
    BOOKING_DELETE_FAILED("BOOKING_016", "Booking deletion failed"),
    BOOKING_CANCELLATION_FAILED("BOOKING_017", "Booking cancellation failed"),
    BOOKING_CONFIRMATION_FAILED("BOOKING_018", "Booking confirmation failed"),
    BOOKING_CHECK_IN_FAILED("BOOKING_019", "Booking check-in failed"),
    BOOKING_CHECK_OUT_FAILED("BOOKING_020", "Booking check-out failed"),
    INVALID_GUEST_DATA("BOOKING_021", "Invalid guest data"),
    INVALID_AMOUNT_CALCULATION("BOOKING_022", "Invalid amount calculation"),
    BOOKING_EXPIRED("BOOKING_023", "Booking has expired"),
    BOOKING_CONFLICT("BOOKING_024", "Booking conflicts with existing reservation"),
    PAYMENT_REQUIRED("BOOKING_025", "Payment is required for this booking"),
    INVALID_BOOKING_REFERENCE("BOOKING_026", "Invalid booking reference"),
    BOOKING_ALREADY_CONFIRMED("BOOKING_027", "Booking is already confirmed"),
    BOOKING_ALREADY_CANCELLED("BOOKING_028", "Booking is already cancelled"),
    BOOKING_ALREADY_CHECKED_IN("BOOKING_029", "Booking is already checked in"),
    BOOKING_ALREADY_CHECKED_OUT("BOOKING_030", "Booking is already checked out"),
    HOTELID_ROOMID_USERID_NOT_EXISTS("BOOKING_031","HotelId or RoomId or UserId not exists");
    
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


