package com.hotel.notification_service.repository;

import com.hotel.notification_service.enums.EventType;
import com.hotel.notification_service.enums.NotificationPriority;
import com.hotel.notification_service.enums.NotificationStatus;
import com.hotel.notification_service.enums.NotificationType;
import com.hotel.notification_service.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    // Basic queries
    java.util.Optional<Notification> findByNotificationReference(String notificationReference);
    java.util.Optional<Notification> findByExternalId(String externalId);
    
    // Status related queries
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status);
    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);
    
    // Type related queries
    List<Notification> findByType(NotificationType type);
    List<Notification> findByTypeAndStatus(NotificationType type, NotificationStatus status);
    Page<Notification> findByType(NotificationType type, Pageable pageable);
    
    // Priority related queries
    List<Notification> findByPriority(NotificationPriority priority);
    List<Notification> findByPriorityAndStatus(NotificationPriority priority, NotificationStatus status);
    
    // Event type queries
    List<Notification> findByEventType(EventType eventType);
    List<Notification> findByEventTypeAndStatus(EventType eventType, NotificationStatus status);
    
    // Recipient queries
    List<Notification> findByRecipient(String recipient);
    List<Notification> findByRecipientAndStatus(String recipient, NotificationStatus status);
    List<Notification> findByRecipientOrderByCreatedAtDesc(String recipient);
    Page<Notification> findByRecipient(String recipient, Pageable pageable);
    
    // Channel queries
    List<Notification> findByChannel(String channel);
    List<Notification> findByChannelAndStatus(String channel, NotificationStatus status);
    
    // Created by queries
    List<Notification> findByCreatedBy(String createdBy);
    List<Notification> findByCreatedByAndStatus(String createdBy, NotificationStatus status);
    
    // Related entity queries
    List<Notification> findByRelatedEntityTypeAndRelatedEntityId(String relatedEntityType, Long relatedEntityId);
    List<Notification> findByRelatedEntityType(String relatedEntityType);
    
    // Date range queries
    List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Notification> findByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, NotificationStatus status);
    List<Notification> findBySentAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Notification> findByScheduledAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Scheduled notifications
    List<Notification> findByScheduledAtBeforeAndStatus(LocalDateTime currentTime, NotificationStatus status);
    List<Notification> findByScheduledAtIsNullAndStatus(NotificationStatus status);
    
    // Failed notifications
    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries);
    List<Notification> findByFailedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Expired notifications
    List<Notification> findByExpiresAtBeforeAndStatus(LocalDateTime currentTime, NotificationStatus status);
    
    // Statistics queries
    long countByStatus(NotificationStatus status);
    long countByTypeAndStatus(NotificationType type, NotificationStatus status);
    long countByRecipientAndStatus(String recipient, NotificationStatus status);
    long countByEventTypeAndStatus(EventType eventType, NotificationStatus status);
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Complex queries using MongoDB query syntax
    @Query("{ $and: [" +
           "{ $or: [ { 'type': ?0 }, { 'type': { $exists: false } } ] }," +
           "{ $or: [ { 'status': ?1 }, { 'status': { $exists: false } } ] }," +
           "{ $or: [ { 'priority': ?2 }, { 'priority': { $exists: false } } ] }," +
           "{ $or: [ { 'eventType': ?3 }, { 'eventType': { $exists: false } } ] }," +
           "{ $or: [ { 'recipient': ?4 }, { 'recipient': { $exists: false } } ] }," +
           "{ $or: [ { 'channel': ?5 }, { 'channel': { $exists: false } } ] }," +
           "{ $or: [ { 'createdBy': ?6 }, { 'createdBy': { $exists: false } } ] }," +
           "{ $or: [ { 'createdAt': { $gte: ?7 } }, { 'createdAt': { $exists: false } } ] }," +
           "{ $or: [ { 'createdAt': { $lte: ?8 } }, { 'createdAt': { $exists: false } } ] }" +
           "] }")
    Page<Notification> findNotificationsWithFilters(
            NotificationType type, NotificationStatus status, NotificationPriority priority,
            EventType eventType, String recipient, String channel, String createdBy,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Recent notifications
    Page<Notification> findByOrderByCreatedAtDesc(Pageable pageable);
    
    // Failed notifications for retry
    @Query("{ 'status': 'FAILED', 'retryCount': { $lt: '$maxRetries' }, 'createdAt': { $gte: ?0 } }")
    List<Notification> findFailedNotificationsForRetry(LocalDateTime since);
    
    // Notifications by template
    List<Notification> findByTemplateIdOrderByCreatedAtDesc(String templateId);
    
    // Notifications by external ID pattern
    @Query("{ 'externalId': { $regex: ?0, $options: 'i' } }")
    List<Notification> findByExternalIdPattern(String pattern);
    
    // Text search across message and subject
    @Query("{ $text: { $search: ?0 } }")
    List<Notification> findByTextSearch(String searchText);
    
    // Notifications by metadata field
    @Query("{ 'metadata.?0': ?1 }")
    List<Notification> findByMetadataField(String field, Object value);
    
    // Notifications by template data field
    @Query("{ 'templateData.?0': ?1 }")
    List<Notification> findByTemplateDataField(String field, Object value);
    
    // Missing methods that were referenced in service
    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 } }")
    long countByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    List<Notification> findByTemplateId(String templateId);
}
