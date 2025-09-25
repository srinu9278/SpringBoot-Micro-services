package com.hotel.notification_service.service;

import com.hotel.notification_service.dto.NotificationListResponseDto;
import com.hotel.notification_service.dto.NotificationRequestDto;
import com.hotel.notification_service.dto.NotificationResponseDto;
import com.hotel.notification_service.enums.EventType;
import com.hotel.notification_service.enums.NotificationPriority;
import com.hotel.notification_service.enums.NotificationStatus;
import com.hotel.notification_service.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    
    // Basic CRUD operations
    NotificationResponseDto createNotification(NotificationRequestDto notificationRequest);
    NotificationResponseDto getNotificationById(String id);
    NotificationResponseDto getNotificationByReference(String notificationReference);
    NotificationResponseDto updateNotification(String id, NotificationRequestDto notificationRequest);
    void deleteNotification(String id);
    
    // Notification processing
    NotificationResponseDto sendNotification(String notificationId);
    NotificationResponseDto retryNotification(String notificationId);
    NotificationResponseDto cancelNotification(String notificationId, String reason);
    NotificationResponseDto updateNotificationStatus(String notificationId, NotificationStatus status);

    void deleteNotification(Long id);

    NotificationResponseDto sendNotification(Long notificationId);

    NotificationResponseDto retryNotification(Long notificationId);

    NotificationResponseDto cancelNotification(Long notificationId, String reason);

    NotificationResponseDto updateNotificationStatus(Long notificationId, NotificationStatus status);

    // Search and filter operations
    Page<NotificationListResponseDto> getAllNotifications(Pageable pageable);
    Page<NotificationListResponseDto> searchNotifications(
            NotificationType type, NotificationStatus status, NotificationPriority priority,
            EventType eventType, String recipient, String channel, String createdBy,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Status related operations
    List<NotificationResponseDto> getNotificationsByStatus(NotificationStatus status);
    Page<NotificationListResponseDto> getNotificationsByStatus(NotificationStatus status, Pageable pageable);
    
    // Type related operations
    List<NotificationResponseDto> getNotificationsByType(NotificationType type);
    List<NotificationResponseDto> getNotificationsByTypeAndStatus(NotificationType type, NotificationStatus status);
    Page<NotificationListResponseDto> getNotificationsByType(NotificationType type, Pageable pageable);
    
    // Priority related operations
    List<NotificationResponseDto> getNotificationsByPriority(NotificationPriority priority);
    List<NotificationResponseDto> getNotificationsByPriorityAndStatus(NotificationPriority priority, NotificationStatus status);
    
    // Event type operations
    List<NotificationResponseDto> getNotificationsByEventType(EventType eventType);
    List<NotificationResponseDto> getNotificationsByEventTypeAndStatus(EventType eventType, NotificationStatus status);
    
    // Recipient operations
    List<NotificationResponseDto> getNotificationsByRecipient(String recipient);
    List<NotificationResponseDto> getNotificationsByRecipientAndStatus(String recipient, NotificationStatus status);
    Page<NotificationListResponseDto> getNotificationsByRecipient(String recipient, Pageable pageable);
    
    // Channel operations
    List<NotificationResponseDto> getNotificationsByChannel(String channel);
    List<NotificationResponseDto> getNotificationsByChannelAndStatus(String channel, NotificationStatus status);
    
    // Created by operations
    List<NotificationResponseDto> getNotificationsByCreatedBy(String createdBy);
    List<NotificationResponseDto> getNotificationsByCreatedByAndStatus(String createdBy, NotificationStatus status);
    
    // Related entity operations
    List<NotificationResponseDto> getNotificationsByRelatedEntity(String relatedEntityType, Long relatedEntityId);
    List<NotificationResponseDto> getNotificationsByRelatedEntityType(String relatedEntityType);
    
    // Date range operations
    List<NotificationResponseDto> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<NotificationResponseDto> getNotificationsByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, NotificationStatus status);
    List<NotificationResponseDto> getSentNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<NotificationResponseDto> getScheduledNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Scheduled notifications
    List<NotificationResponseDto> getScheduledNotifications();
    List<NotificationResponseDto> getPendingNotifications();
    
    // Failed notifications
    List<NotificationResponseDto> getFailedNotifications();
    List<NotificationResponseDto> getFailedNotificationsForRetry();
    List<NotificationResponseDto> getFailedNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Expired notifications
    List<NotificationResponseDto> getExpiredNotifications();
    
    // Statistics operations
    long getNotificationCountByStatus(NotificationStatus status);
    long getNotificationCountByTypeAndStatus(NotificationType type, NotificationStatus status);
    long getNotificationCountByRecipientAndStatus(String recipient, NotificationStatus status);
    long getNotificationCountByEventTypeAndStatus(EventType eventType, NotificationStatus status);
    long getNotificationCountByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    // Recent operations
    Page<NotificationListResponseDto> getRecentNotifications(Pageable pageable);
    
    // Template operations
    List<NotificationResponseDto> getNotificationsByTemplate(String templateId);
    
    // External ID operations
    List<NotificationResponseDto> getNotificationsByExternalIdPattern(String pattern);

    // Utility operations
    @Transactional(readOnly = true)
    boolean isNotificationRetryable(Long notificationId);

    @Transactional(readOnly = true)
    boolean isNotificationExpired(Long notificationId);

    // Utility operations
    String generateNotificationReference();
    boolean isNotificationRetryable(String notificationId);
    boolean isNotificationExpired(String notificationId);
    
    // Event-driven operations
    void handleBookingEvent(String eventType, Object bookingData);
    void handlePaymentEvent(String eventType, Object paymentData);
    void handleRoomEvent(String eventType, Object roomData);
    void handleSystemEvent(String eventType, Object systemData);
    void handleGuestEvent(String eventType, Object guestData);
    void handleMaintenanceEvent(String eventType, Object maintenanceData);
    
    // Test operations
    boolean testSlackConnection();
    String sendTestSlackMessage(String channel, String message);
}
