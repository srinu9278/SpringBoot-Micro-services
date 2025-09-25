package com.hotel.room_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class NotificationClientFallback implements NotificationClient {
    
    @Override
    public void handleRoomEvent(String eventType, Map<String, Object> roomData) {
        log.warn("Notification service is unavailable. Room event '{}' not sent to Slack. Data: {}", eventType, roomData);
    }
    
    @Override
    public void handleSystemEvent(String eventType, Map<String, Object> systemData) {
        log.warn("Notification service is unavailable. System event '{}' not sent to Slack. Data: {}", eventType, systemData);
    }
}
