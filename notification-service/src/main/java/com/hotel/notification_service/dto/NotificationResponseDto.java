package com.hotel.notification_service.dto;

import com.hotel.notification_service.enums.EventType;
import com.hotel.notification_service.enums.NotificationPriority;
import com.hotel.notification_service.enums.NotificationStatus;
import com.hotel.notification_service.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationResponseDto {
    
    private String id;
    private String notificationReference;
    private NotificationType type;
    private NotificationStatus status;
    private NotificationPriority priority;
    private EventType eventType;
    private String recipient;
    private String subject;
    private String message;
    private String templateId;
    private Object templateData;
    private String channel;
    private String externalId;
    private Integer retryCount;
    private Integer maxRetries;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime failedAt;
    private String failureReason;
    private Object metadata;
    private LocalDateTime expiresAt;
    private String createdBy;
    private String relatedEntityType;
    private String relatedEntityId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
