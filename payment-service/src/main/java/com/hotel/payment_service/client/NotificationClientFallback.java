package com.hotel.payment_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {
    
    @Override
    public void handlePaymentEvent(String eventType, Map<String, Object> paymentData) {
        log.warn("Notification service is unavailable. Payment event '{}' not sent to Slack. Data: {}", eventType, paymentData);
    }
}
