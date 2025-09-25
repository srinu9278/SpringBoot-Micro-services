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
public class NotificationListResponseDto {
    
    private String id;
    private String notificationReference;
    private NotificationType type;
    private NotificationStatus status;
    private NotificationPriority priority;
    private EventType eventType;
    private String recipient;
    private String subject;
    private String channel;
    private String externalId;
    private Integer retryCount;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime failedAt;
    private String failureReason;
    private String createdBy;
    private String relatedEntityType;
    private String relatedEntityId;
    private LocalDateTime createdAt;
}
