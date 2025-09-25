package com.hotel.hotel_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {
    
    @Override
    public void handleSystemEvent(String eventType, Map<String, Object> systemData) {
        log.warn("Notification service is unavailable. System event '{}' not sent to Slack. Data: {}", eventType, systemData);
    }
}
