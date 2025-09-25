package com.hotel.user_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {
    
    @Override
    public void handleGuestEvent(String eventType, Map<String, Object> guestData) {
        log.warn("Notification service is unavailable. Guest event '{}' not sent to Slack. Data: {}", eventType, guestData);
    }
}
