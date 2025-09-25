package com.hotel.notification_service.enums;

import lombok.Getter;

@Getter
public enum EventType {
    // Booking Events
    BOOKING_CREATED("Booking Created"),
    BOOKING_UPDATED("Booking Updated"),
    BOOKING_CANCELLED("Booking Cancelled"),
    BOOKING_CONFIRMED("Booking Confirmed"),
    BOOKING_MODIFIED("Booking Modified"),
    CHECK_IN("Check In"),
    CHECK_OUT("Check Out"),
    
    // Payment Events
    PAYMENT_CREATED("Payment Created"),
    PAYMENT_COMPLETED("Payment Completed"),
    PAYMENT_SUCCESS("Payment Successful"),
    PAYMENT_FAILED("Payment Failed"),
    PAYMENT_REFUNDED("Payment Refunded"),
    
    // Room Events
    ROOM_CREATED("Room Created"),
    ROOM_UPDATED("Room Updated"),
    ROOM_DELETED("Room Deleted"),
    ROOM_STATUS_UPDATED("Room Status Updated"),
    ROOM_AVAILABLE("Room Available"),
    ROOM_OCCUPIED("Room Occupied"),
    ROOM_MAINTENANCE("Room Maintenance"),
    ROOM_CLEANING("Room Cleaning"),
    
    // Guest Events
    USER_REGISTERED("User Registered"),
    USER_UPDATED("User Updated"),
    USER_DELETED("User Deleted"),
    GUEST_ARRIVAL("Guest Arrival"),
    GUEST_DEPARTURE("Guest Departure"),
    GUEST_REQUEST("Guest Request"),
    
    // System Events
    SYSTEM_ALERT("System Alert"),
    MAINTENANCE_ALERT("Maintenance Alert"),
    SECURITY_ALERT("Security Alert"),
    WEATHER_ALERT("Weather Alert"),
    
    // Staff Events
    STAFF_ASSIGNMENT("Staff Assignment"),
    SHIFT_CHANGE("Shift Change"),
    TASK_ASSIGNED("Task Assigned"),
    
    // Hotel Events
    HOTEL_CREATED("Hotel Created"),
    HOTEL_UPDATED("Hotel Updated"),
    HOTEL_DELETED("Hotel Deleted"),
    HOTEL_FULL("Hotel Full"),
    HOTEL_AVAILABILITY("Hotel Availability"),
    PROMOTION("Promotion"),
    EVENT_ANNOUNCEMENT("Event Announcement");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }
}

