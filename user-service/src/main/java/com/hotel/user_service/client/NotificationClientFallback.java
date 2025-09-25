package com.hotel.user_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {
    
    @Override
    public void handleUserEvent(String eventType, Map<String, Object> userData) {
        log.warn("Notification service is unavailable. User event '{}' not sent to Slack. Data: {}", eventType, userData);
    }
}
