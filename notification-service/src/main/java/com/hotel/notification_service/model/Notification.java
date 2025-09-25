package com.hotel.notification_service.model;

import com.hotel.notification_service.enums.EventType;
import com.hotel.notification_service.enums.NotificationPriority;
import com.hotel.notification_service.enums.NotificationStatus;
import com.hotel.notification_service.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Notification {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String notificationReference;
    
    private NotificationType type;
    private NotificationStatus status;
    private NotificationPriority priority;
    private EventType eventType;
    
    @Indexed
    private String recipient; // Email, phone, Slack channel, etc.
    
    private String subject;
    private String message;
    private String templateId;
    private Object templateData; // JSON object for template variables
    
    @Indexed
    private String channel; // Slack channel, email address, etc.
    
    @Indexed
    private String externalId; // External service ID (Slack message ID, etc.)
    
    private Integer retryCount = 0;
    private Integer maxRetries = 3;
    
    @Indexed
    private LocalDateTime scheduledAt;
    
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime failedAt;
    private String failureReason;
    
    private Object metadata; // Additional data as JSON object
    
    @Indexed
    private LocalDateTime expiresAt;
    
    @Indexed
    private String createdBy; // User or system that created the notification
    
    @Indexed
    private String relatedEntityType; // booking, payment, room, etc.
    
    private String relatedEntityId; // Changed to String for MongoDB flexibility
    
    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
