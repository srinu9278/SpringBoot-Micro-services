package com.hotel.notification_service.dto;

import com.hotel.notification_service.enums.EventType;
import com.hotel.notification_service.enums.NotificationPriority;
import com.hotel.notification_service.enums.NotificationType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NotificationRequestDto {
    
    @NotNull(message = "Notification type is required")
    private NotificationType type;
    
    @NotNull(message = "Priority is required")
    private NotificationPriority priority;
    
    @NotNull(message = "Event type is required")
    private EventType eventType;
    
    @NotBlank(message = "Recipient is required")
    @Size(max = 255, message = "Recipient must be at most 255 characters")
    private String recipient;
    
    @Size(max = 255, message = "Subject must be at most 255 characters")
    private String subject;
    
    @NotBlank(message = "Message is required")
    @Size(max = 4000, message = "Message must be at most 4000 characters")
    private String message;
    
    private String templateId;
    
    private Object templateData; // JSON object
    
    @Size(max = 100, message = "Channel must be at most 100 characters")
    private String channel;
    
    @Min(value = 0, message = "Max retries must be non-negative")
    @Max(value = 10, message = "Max retries must be at most 10")
    private Integer maxRetries = 3;
    
    private LocalDateTime scheduledAt;
    
    private Object metadata; // JSON object
    
    private LocalDateTime expiresAt;
    
    @Size(max = 100, message = "Created by must be at most 100 characters")
    private String createdBy;
    
    @Size(max = 50, message = "Related entity type must be at most 50 characters")
    private String relatedEntityType;
    
    private String relatedEntityId;
}
