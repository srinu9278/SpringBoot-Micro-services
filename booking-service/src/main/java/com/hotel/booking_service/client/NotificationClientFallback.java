package com.hotel.booking_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {
    
    @Override
    public void handleBookingEvent(String eventType, Map<String, Object> bookingData) {
        log.warn("Notification service is unavailable. Booking event '{}' not sent to Slack. Data: {}", eventType, bookingData);
    }
}
