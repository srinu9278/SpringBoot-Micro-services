package com.hotel.notification_service.serviceImpl;

import com.hotel.notification_service.dto.NotificationListResponseDto;
import com.hotel.notification_service.dto.NotificationRequestDto;
import com.hotel.notification_service.dto.NotificationResponseDto;
import com.hotel.notification_service.dto.SlackResponseDto;
import com.hotel.notification_service.enums.ErrorCode;
import com.hotel.notification_service.enums.EventType;
import com.hotel.notification_service.enums.NotificationPriority;
import com.hotel.notification_service.enums.NotificationStatus;
import com.hotel.notification_service.enums.NotificationType;
import com.hotel.notification_service.exception.CustomException;
import com.hotel.notification_service.model.Notification;
import com.hotel.notification_service.repository.NotificationRepository;
import com.hotel.notification_service.service.NotificationService;
import com.hotel.notification_service.service.SlackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SlackService slackService;
    
    @Override
    public NotificationResponseDto createNotification(NotificationRequestDto notificationRequest) {
        log.info("Creating notification for recipient: {}", notificationRequest.getRecipient());
        
        // Generate notification reference
        String notificationReference = generateNotificationReference();
        
        // Create notification entity
        Notification notification = Notification.builder()
                .notificationReference(notificationReference)
                .type(notificationRequest.getType())
                .status(NotificationStatus.PENDING)
                .priority(notificationRequest.getPriority())
                .eventType(notificationRequest.getEventType())
                .recipient(notificationRequest.getRecipient())
                .subject(notificationRequest.getSubject())
                .message(notificationRequest.getMessage())
                .templateId(notificationRequest.getTemplateId())
                .templateData(notificationRequest.getTemplateData())
                .channel(notificationRequest.getChannel())
                .maxRetries(notificationRequest.getMaxRetries())
                .scheduledAt(notificationRequest.getScheduledAt())
                .metadata(notificationRequest.getMetadata())
                .expiresAt(notificationRequest.getExpiresAt())
                .createdBy(notificationRequest.getCreatedBy())
                .relatedEntityType(notificationRequest.getRelatedEntityType())
                .relatedEntityId(notificationRequest.getRelatedEntityId())
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification created successfully with ID: {}", savedNotification.getId());
        
        // Auto-send Slack notifications
        if (savedNotification.getType() == NotificationType.SLACK) {
            try {
                log.info("Auto-sending Slack notification for ID: {}", savedNotification.getId());
                return sendNotification(savedNotification.getId());
            } catch (Exception e) {
                log.error("Failed to auto-send Slack notification: {}", e.getMessage());
                // Return the created notification even if sending fails
            }
        }
        
        return convertToResponseDto(savedNotification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDto getNotificationById(String id) {
        log.info("Fetching notification by ID: {}", id);
        
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + id));
        
        return convertToResponseDto(notification);
    }
    
    @Override
    @Transactional(readOnly = true)
    public NotificationResponseDto getNotificationByReference(String notificationReference) {
        log.info("Fetching notification by reference: {}", notificationReference);
        
        Notification notification = notificationRepository.findByNotificationReference(notificationReference)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with reference: " + notificationReference));
        
        return convertToResponseDto(notification);
    }
    
    @Override
    public NotificationResponseDto updateNotification(String id, NotificationRequestDto notificationRequest) {
        log.info("Updating notification with ID: {}", id);
        
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + id));
        
        // Update notification fields
        Notification updatedNotification = existingNotification.toBuilder()
                .type(notificationRequest.getType())
                .priority(notificationRequest.getPriority())
                .eventType(notificationRequest.getEventType())
                .recipient(notificationRequest.getRecipient())
                .subject(notificationRequest.getSubject())
                .message(notificationRequest.getMessage())
                .templateId(notificationRequest.getTemplateId())
                .templateData(notificationRequest.getTemplateData())
                .channel(notificationRequest.getChannel())
                .maxRetries(notificationRequest.getMaxRetries())
                .scheduledAt(notificationRequest.getScheduledAt())
                .metadata(notificationRequest.getMetadata())
                .expiresAt(notificationRequest.getExpiresAt())
                .createdBy(notificationRequest.getCreatedBy())
                .relatedEntityType(notificationRequest.getRelatedEntityType())
                .relatedEntityId(notificationRequest.getRelatedEntityId())
                .build();
        
        Notification savedNotification = notificationRepository.save(updatedNotification);
        log.info("Notification updated successfully with ID: {}", savedNotification.getId());
        
        return convertToResponseDto(savedNotification);
    }

    @Override
    public void deleteNotification(String id) {

    }

    @Override
    public NotificationResponseDto sendNotification(String notificationId) {
        log.info("Sending notification with ID: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + notificationId));
        
        if (notification.getStatus() != NotificationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_STATUS, "Notification is not in pending status");
        }
        
        if (isNotificationExpired(notificationId)) {
            notification = notification.toBuilder()
                    .status(NotificationStatus.FAILED)
                    .failedAt(LocalDateTime.now())
                    .failureReason("Notification expired")
                    .build();
            notificationRepository.save(notification);
            throw new CustomException(ErrorCode.NOTIFICATION_EXPIRED, "Notification has expired");
        }
        
        try {
            // Send notification based on type
            boolean sent = false;
            String externalId = null;
            
            switch (notification.getType()) {
                case SLACK:
                    SlackResponseDto slackResponse = slackService.sendMessage(
                            notification.getChannel() != null ? notification.getChannel() : "#general",
                            notification.getMessage()
                    );
                    sent = slackResponse.isSuccess();
                    externalId = slackResponse.getMessageId();
                    break;
                case EMAIL:
                    // TODO: Implement email sending
                    sent = true;
                    break;
                case SMS:
                    // TODO: Implement SMS sending
                    sent = true;
                    break;
                case PUSH:
                    // TODO: Implement push notification
                    sent = true;
                    break;
                case WEBHOOK:
                    // TODO: Implement webhook
                    sent = true;
                    break;
                case IN_APP:
                    // TODO: Implement in-app notification
                    sent = true;
                    break;
            }
            
            if (sent) {
                notification = notification.toBuilder()
                        .status(NotificationStatus.SENT)
                        .sentAt(LocalDateTime.now())
                        .externalId(externalId)
                        .build();
                
                Notification savedNotification = notificationRepository.save(notification);
                log.info("Notification sent successfully with ID: {}", savedNotification.getId());
                
                return convertToResponseDto(savedNotification);
            } else {
                throw new CustomException(ErrorCode.NOTIFICATION_SENDING_FAILED, "Failed to send notification");
            }
            
        } catch (Exception e) {
            log.error("Error sending notification with ID: {}", notificationId, e);
            
            notification = notification.toBuilder()
                    .status(NotificationStatus.FAILED)
                    .failedAt(LocalDateTime.now())
                    .failureReason(e.getMessage())
                    .retryCount(notification.getRetryCount() + 1)
                    .build();
            
            notificationRepository.save(notification);
            throw new CustomException(ErrorCode.NOTIFICATION_SENDING_FAILED, "Failed to send notification: " + e.getMessage());
        }
    }

    @Override
    public NotificationResponseDto retryNotification(String notificationId) {
        return null;
    }

    @Override
    public NotificationResponseDto cancelNotification(String notificationId, String reason) {
        return null;
    }

    @Override
    public NotificationResponseDto updateNotificationStatus(String notificationId, NotificationStatus status) {
        return null;
    }

    @Override
    public void deleteNotification(Long id) {
        log.info("Deleting notification with ID: {}", id);
        
        Notification notification = notificationRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + id));
        
        notificationRepository.delete(notification);
        log.info("Notification deleted successfully with ID: {}", id);
    }
    
    @Override
    public NotificationResponseDto sendNotification(Long notificationId) {
        log.info("Sending notification with ID: {}", notificationId);
        
        Notification notification = notificationRepository.findById(String.valueOf(notificationId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + notificationId));
        
        if (notification.getStatus() != NotificationStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_STATUS, "Notification is not in pending status");
        }
        
        if (isNotificationExpired(notificationId)) {
            notification = notification.toBuilder()
                    .status(NotificationStatus.FAILED)
                    .failedAt(LocalDateTime.now())
                    .failureReason("Notification expired")
                    .build();
            notificationRepository.save(notification);
            throw new CustomException(ErrorCode.NOTIFICATION_EXPIRED, "Notification has expired");
        }
        
        try {
            // Send notification based on type
            boolean sent = false;
            String externalId = null;
            
            switch (notification.getType()) {
                case SLACK:
                    SlackResponseDto slackResponse = slackService.sendMessage(
                            notification.getChannel() != null ? notification.getChannel() : "#general",
                            notification.getMessage()
                    );
                    sent = slackResponse.isSuccess();
                    externalId = slackResponse.getMessageId();
                    break;
                case EMAIL:
                    // TODO: Implement email sending
                    sent = true;
                    break;
                case SMS:
                    // TODO: Implement SMS sending
                    sent = true;
                    break;
                case PUSH:
                    // TODO: Implement push notification
                    sent = true;
                    break;
                case WEBHOOK:
                    // TODO: Implement webhook
                    sent = true;
                    break;
                case IN_APP:
                    // TODO: Implement in-app notification
                    sent = true;
                    break;
            }
            
            if (sent) {
                notification = notification.toBuilder()
                        .status(NotificationStatus.SENT)
                        .sentAt(LocalDateTime.now())
                        .externalId(externalId)
                        .build();
                
                Notification savedNotification = notificationRepository.save(notification);
                log.info("Notification sent successfully with ID: {}", savedNotification.getId());
                
                return convertToResponseDto(savedNotification);
            } else {
                throw new CustomException(ErrorCode.NOTIFICATION_SENDING_FAILED, "Failed to send notification");
            }
            
        } catch (Exception e) {
            log.error("Error sending notification with ID: {}", notificationId, e);
            
            notification = notification.toBuilder()
                    .status(NotificationStatus.FAILED)
                    .failedAt(LocalDateTime.now())
                    .failureReason(e.getMessage())
                    .retryCount(notification.getRetryCount() + 1)
                    .build();
            
            notificationRepository.save(notification);
            throw new CustomException(ErrorCode.NOTIFICATION_SENDING_FAILED, "Failed to send notification: " + e.getMessage());
        }
    }
    
    @Override
    public NotificationResponseDto retryNotification(Long notificationId) {
        log.info("Retrying notification with ID: {}", notificationId);
        
        Notification notification = notificationRepository.findById(String.valueOf(notificationId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + notificationId));
        
        if (!isNotificationRetryable(notificationId)) {
            throw new CustomException(ErrorCode.MAX_RETRIES_EXCEEDED, "Maximum retry attempts exceeded");
        }
        
        // Reset status to pending for retry
        notification = notification.toBuilder()
                .status(NotificationStatus.PENDING)
                .retryCount(notification.getRetryCount() + 1)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification retry initiated for ID: {}", savedNotification.getId());
        
        // Send the notification
        return sendNotification(notificationId);
    }
    
    @Override
    public NotificationResponseDto cancelNotification(Long notificationId, String reason) {
        log.info("Cancelling notification with ID: {}", notificationId);
        
        Notification notification = notificationRepository.findById(String.valueOf(notificationId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + notificationId));
        
        if (notification.getStatus() == NotificationStatus.SENT) {
            throw new CustomException(ErrorCode.INVALID_NOTIFICATION_STATUS, "Cannot cancel sent notification");
        }
        
        notification = notification.toBuilder()
                .status(NotificationStatus.CANCELLED)
                .failureReason(reason)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification cancelled successfully with ID: {}", savedNotification.getId());
        
        return convertToResponseDto(savedNotification);
    }
    
    @Override
    public NotificationResponseDto updateNotificationStatus(Long notificationId, NotificationStatus status) {
        log.info("Updating notification status for ID: {} to {}", notificationId, status);
        
        Notification notification = notificationRepository.findById(String.valueOf(notificationId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + notificationId));
        
        notification = notification.toBuilder()
                .status(status)
                .build();
        
        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification status updated successfully for ID: {}", savedNotification.getId());
        
        return convertToResponseDto(savedNotification);
    }
    
    // Search and filter operations
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getAllNotifications(Pageable pageable) {
        log.info("Fetching all notifications with pagination");
        
        Page<Notification> notifications = notificationRepository.findAll(pageable);
        return notifications.map(this::convertToListResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> searchNotifications(NotificationType type, NotificationStatus status, 
                                                               NotificationPriority priority, EventType eventType,
                                                               String recipient, String channel, String createdBy,
                                                               LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        log.info("Searching notifications with filters");
        
        Page<Notification> notifications = notificationRepository.findNotificationsWithFilters(
                type, status, priority, eventType, recipient, channel, createdBy,
                startDate, endDate, pageable);
        
        return notifications.map(this::convertToListResponseDto);
    }
    
    // Status related operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByStatus(NotificationStatus status) {
        log.info("Fetching notifications with status: {}", status);
        
        List<Notification> notifications = notificationRepository.findByStatusOrderByCreatedAtDesc(status);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getNotificationsByStatus(NotificationStatus status, Pageable pageable) {
        log.info("Fetching notifications with status: {} and pagination", status);
        
        Page<Notification> notifications = notificationRepository.findByStatus(status, pageable);
        return notifications.map(this::convertToListResponseDto);
    }
    
    // Type related operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByType(NotificationType type) {
        log.info("Fetching notifications with type: {}", type);
        
        List<Notification> notifications = notificationRepository.findByType(type);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByTypeAndStatus(NotificationType type, NotificationStatus status) {
        log.info("Fetching notifications with type: {} and status: {}", type, status);
        
        List<Notification> notifications = notificationRepository.findByTypeAndStatus(type, status);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getNotificationsByType(NotificationType type, Pageable pageable) {
        log.info("Fetching notifications with type: {} and pagination", type);
        
        Page<Notification> notifications = notificationRepository.findByType(type, pageable);
        return notifications.map(this::convertToListResponseDto);
    }
    
    // Priority related operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByPriority(NotificationPriority priority) {
        log.info("Fetching notifications with priority: {}", priority);
        
        List<Notification> notifications = notificationRepository.findByPriority(priority);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByPriorityAndStatus(NotificationPriority priority, NotificationStatus status) {
        log.info("Fetching notifications with priority: {} and status: {}", priority, status);
        
        List<Notification> notifications = notificationRepository.findByPriorityAndStatus(priority, status);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Event type operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByEventType(EventType eventType) {
        log.info("Fetching notifications with event type: {}", eventType);
        
        List<Notification> notifications = notificationRepository.findByEventType(eventType);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByEventTypeAndStatus(EventType eventType, NotificationStatus status) {
        log.info("Fetching notifications with event type: {} and status: {}", eventType, status);
        
        List<Notification> notifications = notificationRepository.findByEventTypeAndStatus(eventType, status);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Recipient operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByRecipient(String recipient) {
        log.info("Fetching notifications for recipient: {}", recipient);
        
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByRecipientAndStatus(String recipient, NotificationStatus status) {
        log.info("Fetching notifications for recipient: {} with status: {}", recipient, status);
        
        List<Notification> notifications = notificationRepository.findByRecipientAndStatus(recipient, status);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getNotificationsByRecipient(String recipient, Pageable pageable) {
        log.info("Fetching notifications for recipient: {} with pagination", recipient);
        
        Page<Notification> notifications = notificationRepository.findByRecipient(recipient, pageable);
        return notifications.map(this::convertToListResponseDto);
    }
    
    // Channel operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByChannel(String channel) {
        log.info("Fetching notifications for channel: {}", channel);
        
        List<Notification> notifications = notificationRepository.findByChannel(channel);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByChannelAndStatus(String channel, NotificationStatus status) {
        log.info("Fetching notifications for channel: {} with status: {}", channel, status);
        
        List<Notification> notifications = notificationRepository.findByChannelAndStatus(channel, status);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Created by operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByCreatedBy(String createdBy) {
        log.info("Fetching notifications created by: {}", createdBy);
        
        List<Notification> notifications = notificationRepository.findByCreatedBy(createdBy);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByCreatedByAndStatus(String createdBy, NotificationStatus status) {
        log.info("Fetching notifications created by: {} with status: {}", createdBy, status);
        
        List<Notification> notifications = notificationRepository.findByCreatedByAndStatus(createdBy, status);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Related entity operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByRelatedEntity(String relatedEntityType, Long relatedEntityId) {
        log.info("Fetching notifications for related entity: {} with ID: {}", relatedEntityType, relatedEntityId);
        
        List<Notification> notifications = notificationRepository.findByRelatedEntityTypeAndRelatedEntityId(relatedEntityType, relatedEntityId);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByRelatedEntityType(String relatedEntityType) {
        log.info("Fetching notifications for related entity type: {}", relatedEntityType);
        
        List<Notification> notifications = notificationRepository.findByRelatedEntityType(relatedEntityType);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Date range operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching notifications between {} and {}", startDate, endDate);
        
        List<Notification> notifications = notificationRepository.findByCreatedAtBetween(startDate, endDate);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, NotificationStatus status) {
        log.info("Fetching notifications between {} and {} with status: {}", startDate, endDate, status);
        
        List<Notification> notifications = notificationRepository.findByCreatedAtBetweenAndStatus(startDate, endDate, status);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getSentNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching sent notifications between {} and {}", startDate, endDate);
        
        List<Notification> notifications = notificationRepository.findBySentAtBetween(startDate, endDate);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getScheduledNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching scheduled notifications between {} and {}", startDate, endDate);
        
        List<Notification> notifications = notificationRepository.findByScheduledAtBetween(startDate, endDate);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Scheduled notifications
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getScheduledNotifications() {
        log.info("Fetching scheduled notifications");
        
        List<Notification> notifications = notificationRepository.findByScheduledAtBeforeAndStatus(LocalDateTime.now(), NotificationStatus.PENDING);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getPendingNotifications() {
        log.info("Fetching pending notifications");
        
        List<Notification> notifications = notificationRepository.findByScheduledAtIsNullAndStatus(NotificationStatus.PENDING);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Failed notifications
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getFailedNotifications() {
        log.info("Fetching failed notifications");
        
        List<Notification> notifications = notificationRepository.findByStatus(NotificationStatus.FAILED);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getFailedNotificationsForRetry() {
        log.info("Fetching failed notifications for retry");
        
        List<Notification> notifications = notificationRepository.findFailedNotificationsForRetry(LocalDateTime.now().minusHours(1));
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getFailedNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching failed notifications between {} and {}", startDate, endDate);
        
        List<Notification> notifications = notificationRepository.findByFailedAtBetween(startDate, endDate);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Expired notifications
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getExpiredNotifications() {
        log.info("Fetching expired notifications");
        
        List<Notification> notifications = notificationRepository.findByExpiresAtBeforeAndStatus(LocalDateTime.now(), NotificationStatus.PENDING);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Statistics operations
    @Override
    @Transactional(readOnly = true)
    public long getNotificationCountByStatus(NotificationStatus status) {
        log.info("Getting notification count for status: {}", status);
        return notificationRepository.countByStatus(status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getNotificationCountByTypeAndStatus(NotificationType type, NotificationStatus status) {
        log.info("Getting notification count for type: {} and status: {}", type, status);
        return notificationRepository.countByTypeAndStatus(type, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getNotificationCountByRecipientAndStatus(String recipient, NotificationStatus status) {
        log.info("Getting notification count for recipient: {} and status: {}", recipient, status);
        return notificationRepository.countByRecipientAndStatus(recipient, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getNotificationCountByEventTypeAndStatus(EventType eventType, NotificationStatus status) {
        log.info("Getting notification count for event type: {} and status: {}", eventType, status);
        return notificationRepository.countByEventTypeAndStatus(eventType, status);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getNotificationCountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting notification count between {} and {}", startDate, endDate);
        return notificationRepository.countByDateRange(startDate, endDate);
    }
    
    // Recent operations
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationListResponseDto> getRecentNotifications(Pageable pageable) {
        log.info("Fetching recent notifications with pagination");
        
        Page<Notification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
        return notifications.map(this::convertToListResponseDto);
    }
    
    // Template operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByTemplate(String templateId) {
        log.info("Fetching notifications for template: {}", templateId);
        
        List<Notification> notifications = notificationRepository.findByTemplateId(templateId);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // External ID operations
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotificationsByExternalIdPattern(String pattern) {
        log.info("Fetching notifications for external ID pattern: {}", pattern);
        
        List<Notification> notifications = notificationRepository.findByExternalIdPattern(pattern);
        return notifications.stream().map(this::convertToResponseDto).collect(Collectors.toList());
    }
    
    // Utility operations
    @Transactional(readOnly = true)
    @Override
    public boolean isNotificationRetryable(Long notificationId) {
        Notification notification = notificationRepository.findById(String.valueOf(notificationId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + notificationId));
        
        return notification.getStatus() == NotificationStatus.FAILED && 
               notification.getRetryCount() < notification.getMaxRetries();
    }
    
    @Transactional(readOnly = true)
    @Override
    public boolean isNotificationExpired(Long notificationId) {
        Notification notification = notificationRepository.findById(String.valueOf(notificationId))
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + notificationId));
        
        return notification.getExpiresAt() != null && notification.getExpiresAt().isBefore(LocalDateTime.now());
    }
    
    @Override
    public String generateNotificationReference() {
        return "NOTIF_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public boolean isNotificationRetryable(String notificationId) {
        return false;
    }

    @Override
    public boolean isNotificationExpired(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND, "Notification not found with ID: " + notificationId));
        
        return notification.getExpiresAt() != null && notification.getExpiresAt().isBefore(LocalDateTime.now());
    }

    // Event-driven operations
    @Override
    public void handleBookingEvent(String eventType, Object bookingData) {
        log.info("Handling booking event: {}", eventType);
        
        // Create notification for booking events
        NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                .type(NotificationType.SLACK)
                .priority(NotificationPriority.NORMAL)
                .eventType(EventType.valueOf(eventType.toUpperCase()))
                .recipient("#bookings")
                .message("Booking event: " + eventType)
                .channel("#bookings")
                .createdBy("system")
                .relatedEntityType("booking")
                .build();
        
        createNotification(notificationRequest);
    }
    
    @Override
    public void handlePaymentEvent(String eventType, Object paymentData) {
        log.info("Handling payment event: {}", eventType);
        
        // Create notification for payment events
        NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                .type(NotificationType.SLACK)
                .priority(NotificationPriority.HIGH)
                .eventType(EventType.valueOf(eventType.toUpperCase()))
                .recipient("#payments")
                .message("Payment event: " + eventType)
                .channel("#payments")
                .createdBy("system")
                .relatedEntityType("payment")
                .build();
        
        createNotification(notificationRequest);
    }
    
    @Override
    public void handleRoomEvent(String eventType, Object roomData) {
        log.info("Handling room event: {}", eventType);
        
        // Create notification for room events
        NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                .type(NotificationType.SLACK)
                .priority(NotificationPriority.NORMAL)
                .eventType(EventType.valueOf(eventType.toUpperCase()))
                .recipient("#rooms")
                .message("Room event: " + eventType)
                .channel("#rooms")
                .createdBy("system")
                .relatedEntityType("room")
                .build();
        
        createNotification(notificationRequest);
    }
    
    @Override
    public void handleSystemEvent(String eventType, Object systemData) {
        log.info("Handling system event: {}", eventType);
        
        // Create notification for system events
        NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                .type(NotificationType.SLACK)
                .priority(NotificationPriority.URGENT)
                .eventType(EventType.SYSTEM_ALERT)
                .recipient("#alerts")
                .message("System event: " + eventType)
                .channel("#alerts")
                .createdBy("system")
                .relatedEntityType("system")
                .build();
        
        createNotification(notificationRequest);
    }
    
    @Override
    public void handleGuestEvent(String eventType, Object guestData) {
        log.info("Handling guest event: {}", eventType);
        
        // Create notification for guest events
        NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                .type(NotificationType.SLACK)
                .priority(NotificationPriority.NORMAL)
                .eventType(EventType.valueOf(eventType.toUpperCase()))
                .recipient("#guests")
                .message("Guest event: " + eventType)
                .channel("#guests")
                .createdBy("system")
                .relatedEntityType("guest")
                .build();
        
        createNotification(notificationRequest);
    }
    
    @Override
    public void handleMaintenanceEvent(String eventType, Object maintenanceData) {
        log.info("Handling maintenance event: {}", eventType);
        
        // Create notification for maintenance events
        NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                .type(NotificationType.SLACK)
                .priority(NotificationPriority.HIGH)
                .eventType(EventType.MAINTENANCE_ALERT)
                .recipient("#maintenance")
                .message("Maintenance event: " + eventType)
                .channel("#maintenance")
                .createdBy("system")
                .relatedEntityType("maintenance")
                .build();
        
        createNotification(notificationRequest);
    }
    
    // Test operations
    @Override
    public boolean testSlackConnection() {
        log.info("Testing Slack connection");
        return slackService.testConnection();
    }
    
    @Override
    public String sendTestSlackMessage(String channel, String message) {
        log.info("Sending test Slack message to channel: {}", channel);
        try {
            SlackResponseDto response = slackService.sendMessage(channel, message);
            if (response.isSuccess()) {
                return "Message sent successfully! Message ID: " + response.getMessageId();
            } else {
                return "Failed to send message: " + response.getError();
            }
        } catch (Exception e) {
            log.error("Error sending test Slack message: {}", e.getMessage(), e);
            throw new CustomException(ErrorCode.NOTIFICATION_SENDING_FAILED, "Failed to send test message: " + e.getMessage());
        }
    }
    
    // Helper methods
    private NotificationResponseDto convertToResponseDto(Notification notification) {
        return NotificationResponseDto.builder()
                .id(notification.getId())
                .notificationReference(notification.getNotificationReference())
                .type(notification.getType())
                .status(notification.getStatus())
                .priority(notification.getPriority())
                .eventType(notification.getEventType())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .templateId(notification.getTemplateId())
                .templateData(notification.getTemplateData())
                .channel(notification.getChannel())
                .externalId(notification.getExternalId())
                .retryCount(notification.getRetryCount())
                .maxRetries(notification.getMaxRetries())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .deliveredAt(notification.getDeliveredAt())
                .failedAt(notification.getFailedAt())
                .failureReason(notification.getFailureReason())
                .metadata(notification.getMetadata())
                .expiresAt(notification.getExpiresAt())
                .createdBy(notification.getCreatedBy())
                .relatedEntityType(notification.getRelatedEntityType())
                .relatedEntityId(notification.getRelatedEntityId())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
    
    private NotificationListResponseDto convertToListResponseDto(Notification notification) {
        return NotificationListResponseDto.builder()
                .id(notification.getId())
                .notificationReference(notification.getNotificationReference())
                .type(notification.getType())
                .status(notification.getStatus())
                .priority(notification.getPriority())
                .eventType(notification.getEventType())
                .recipient(notification.getRecipient())
                .subject(notification.getSubject())
                .channel(notification.getChannel())
                .externalId(notification.getExternalId())
                .retryCount(notification.getRetryCount())
                .scheduledAt(notification.getScheduledAt())
                .sentAt(notification.getSentAt())
                .deliveredAt(notification.getDeliveredAt())
                .failedAt(notification.getFailedAt())
                .failureReason(notification.getFailureReason())
                .createdBy(notification.getCreatedBy())
                .relatedEntityType(notification.getRelatedEntityType())
                .relatedEntityId(notification.getRelatedEntityId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
