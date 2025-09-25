package com.hotel.notification_service.controller;

import com.hotel.notification_service.dto.NotificationListResponseDto;
import com.hotel.notification_service.dto.NotificationRequestDto;
import com.hotel.notification_service.dto.NotificationResponseDto;
import com.hotel.notification_service.enums.EventType;
import com.hotel.notification_service.enums.NotificationPriority;
import com.hotel.notification_service.enums.NotificationStatus;
import com.hotel.notification_service.enums.NotificationType;
import com.hotel.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v2/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    
    private final NotificationService notificationService;
    
    // Basic CRUD operations
    @PostMapping("/create")
    public ResponseEntity<NotificationResponseDto> createNotification(@Valid @RequestBody NotificationRequestDto notificationRequest) {
        log.info("Creating notification for recipient: {}", notificationRequest.getRecipient());
        NotificationResponseDto response = notificationService.createNotification(notificationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDto> getNotificationById(@PathVariable String id) {
        log.info("Fetching notification by ID: {}", id);
        NotificationResponseDto response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/reference/{reference}")
    public ResponseEntity<NotificationResponseDto> getNotificationByReference(@PathVariable String reference) {
        log.info("Fetching notification by reference: {}", reference);
        NotificationResponseDto response = notificationService.getNotificationByReference(reference);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<NotificationResponseDto> updateNotification(@PathVariable String id, @Valid @RequestBody NotificationRequestDto notificationRequest) {
        log.info("Updating notification with ID: {}", id);
        NotificationResponseDto response = notificationService.updateNotification(id, notificationRequest);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable String id) {
        log.info("Deleting notification with ID: {}", id);
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
    
    // Notification processing
    @PostMapping("/send/{id}")
    public ResponseEntity<NotificationResponseDto> sendNotification(@PathVariable String id) {
        log.info("Sending notification with ID: {}", id);
        NotificationResponseDto response = notificationService.sendNotification(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/retry/{id}")
    public ResponseEntity<NotificationResponseDto> retryNotification(@PathVariable String id) {
        log.info("Retrying notification with ID: {}", id);
        NotificationResponseDto response = notificationService.retryNotification(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/cancel/{id}")
    public ResponseEntity<NotificationResponseDto> cancelNotification(@PathVariable String id, @RequestParam String reason) {
        log.info("Cancelling notification with ID: {} for reason: {}", id, reason);
        NotificationResponseDto response = notificationService.cancelNotification(id, reason);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/status/{id}")
    public ResponseEntity<NotificationResponseDto> updateNotificationStatus(@PathVariable String id, @RequestParam NotificationStatus status) {
        log.info("Updating notification status for ID: {} to {}", id, status);
        NotificationResponseDto response = notificationService.updateNotificationStatus(id, status);
        return ResponseEntity.ok(response);
    }
    
    // Search and filter operations
    @GetMapping("/all")
    public ResponseEntity<Page<NotificationListResponseDto>> getAllNotifications(Pageable pageable) {
        log.info("Fetching all notifications with pagination");
        Page<NotificationListResponseDto> response = notificationService.getAllNotifications(pageable);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<NotificationListResponseDto>> searchNotifications(
            @RequestParam(required = false) NotificationType type,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationPriority priority,
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) String channel,
            @RequestParam(required = false) String createdBy,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            Pageable pageable) {
        log.info("Searching notifications with filters");
        Page<NotificationListResponseDto> response = notificationService.searchNotifications(
                type, status, priority, eventType, recipient, channel, createdBy, startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }
    
    // Status related operations
    @GetMapping("/status/{status}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        log.info("Fetching notifications with status: {}", status);
        List<NotificationResponseDto> response = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}/page")
    public ResponseEntity<Page<NotificationListResponseDto>> getNotificationsByStatus(@PathVariable NotificationStatus status, Pageable pageable) {
        log.info("Fetching notifications with status: {} and pagination", status);
        Page<NotificationListResponseDto> response = notificationService.getNotificationsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }
    
    // Type related operations
    @GetMapping("/type/{type}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByType(@PathVariable NotificationType type) {
        log.info("Fetching notifications with type: {}", type);
        List<NotificationResponseDto> response = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/type/{type}/status/{status}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByTypeAndStatus(@PathVariable NotificationType type, @PathVariable NotificationStatus status) {
        log.info("Fetching notifications with type: {} and status: {}", type, status);
        List<NotificationResponseDto> response = notificationService.getNotificationsByTypeAndStatus(type, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/type/{type}/page")
    public ResponseEntity<Page<NotificationListResponseDto>> getNotificationsByType(@PathVariable NotificationType type, Pageable pageable) {
        log.info("Fetching notifications with type: {} and pagination", type);
        Page<NotificationListResponseDto> response = notificationService.getNotificationsByType(type, pageable);
        return ResponseEntity.ok(response);
    }
    
    // Priority related operations
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByPriority(@PathVariable NotificationPriority priority) {
        log.info("Fetching notifications with priority: {}", priority);
        List<NotificationResponseDto> response = notificationService.getNotificationsByPriority(priority);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/priority/{priority}/status/{status}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByPriorityAndStatus(@PathVariable NotificationPriority priority, @PathVariable NotificationStatus status) {
        log.info("Fetching notifications with priority: {} and status: {}", priority, status);
        List<NotificationResponseDto> response = notificationService.getNotificationsByPriorityAndStatus(priority, status);
        return ResponseEntity.ok(response);
    }
    
    // Event type operations
    @GetMapping("/event/{eventType}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByEventType(@PathVariable EventType eventType) {
        log.info("Fetching notifications with event type: {}", eventType);
        List<NotificationResponseDto> response = notificationService.getNotificationsByEventType(eventType);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/event/{eventType}/status/{status}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByEventTypeAndStatus(@PathVariable EventType eventType, @PathVariable NotificationStatus status) {
        log.info("Fetching notifications with event type: {} and status: {}", eventType, status);
        List<NotificationResponseDto> response = notificationService.getNotificationsByEventTypeAndStatus(eventType, status);
        return ResponseEntity.ok(response);
    }
    
    // Recipient operations
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByRecipient(@PathVariable String recipient) {
        log.info("Fetching notifications for recipient: {}", recipient);
        List<NotificationResponseDto> response = notificationService.getNotificationsByRecipient(recipient);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recipient/{recipient}/status/{status}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByRecipientAndStatus(@PathVariable String recipient, @PathVariable NotificationStatus status) {
        log.info("Fetching notifications for recipient: {} with status: {}", recipient, status);
        List<NotificationResponseDto> response = notificationService.getNotificationsByRecipientAndStatus(recipient, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/recipient/{recipient}/page")
    public ResponseEntity<Page<NotificationListResponseDto>> getNotificationsByRecipient(@PathVariable String recipient, Pageable pageable) {
        log.info("Fetching notifications for recipient: {} with pagination", recipient);
        Page<NotificationListResponseDto> response = notificationService.getNotificationsByRecipient(recipient, pageable);
        return ResponseEntity.ok(response);
    }
    
    // Channel operations
    @GetMapping("/channel/{channel}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByChannel(@PathVariable String channel) {
        log.info("Fetching notifications for channel: {}", channel);
        List<NotificationResponseDto> response = notificationService.getNotificationsByChannel(channel);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/channel/{channel}/status/{status}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByChannelAndStatus(@PathVariable String channel, @PathVariable NotificationStatus status) {
        log.info("Fetching notifications for channel: {} with status: {}", channel, status);
        List<NotificationResponseDto> response = notificationService.getNotificationsByChannelAndStatus(channel, status);
        return ResponseEntity.ok(response);
    }
    
    // Created by operations
    @GetMapping("/created-by/{createdBy}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByCreatedBy(@PathVariable String createdBy) {
        log.info("Fetching notifications created by: {}", createdBy);
        List<NotificationResponseDto> response = notificationService.getNotificationsByCreatedBy(createdBy);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/created-by/{createdBy}/status/{status}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByCreatedByAndStatus(@PathVariable String createdBy, @PathVariable NotificationStatus status) {
        log.info("Fetching notifications created by: {} with status: {}", createdBy, status);
        List<NotificationResponseDto> response = notificationService.getNotificationsByCreatedByAndStatus(createdBy, status);
        return ResponseEntity.ok(response);
    }
    
    // Related entity operations
    @GetMapping("/related/{entityType}/{entityId}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByRelatedEntity(@PathVariable String entityType, @PathVariable Long entityId) {
        log.info("Fetching notifications for related entity: {} with ID: {}", entityType, entityId);
        List<NotificationResponseDto> response = notificationService.getNotificationsByRelatedEntity(entityType, entityId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/related/{entityType}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByRelatedEntityType(@PathVariable String entityType) {
        log.info("Fetching notifications for related entity type: {}", entityType);
        List<NotificationResponseDto> response = notificationService.getNotificationsByRelatedEntityType(entityType);
        return ResponseEntity.ok(response);
    }
    
    // Date range operations
    @GetMapping("/date-range")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByDateRange(
            @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        log.info("Fetching notifications between {} and {}", startDate, endDate);
        List<NotificationResponseDto> response = notificationService.getNotificationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/date-range/status/{status}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByDateRangeAndStatus(
            @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate, @PathVariable NotificationStatus status) {
        log.info("Fetching notifications between {} and {} with status: {}", startDate, endDate, status);
        List<NotificationResponseDto> response = notificationService.getNotificationsByDateRangeAndStatus(startDate, endDate, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sent/date-range")
    public ResponseEntity<List<NotificationResponseDto>> getSentNotificationsByDateRange(
            @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        log.info("Fetching sent notifications between {} and {}", startDate, endDate);
        List<NotificationResponseDto> response = notificationService.getSentNotificationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/scheduled/date-range")
    public ResponseEntity<List<NotificationResponseDto>> getScheduledNotificationsByDateRange(
            @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        log.info("Fetching scheduled notifications between {} and {}", startDate, endDate);
        List<NotificationResponseDto> response = notificationService.getScheduledNotificationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    // Scheduled notifications
    @GetMapping("/scheduled")
    public ResponseEntity<List<NotificationResponseDto>> getScheduledNotifications() {
        log.info("Fetching scheduled notifications");
        List<NotificationResponseDto> response = notificationService.getScheduledNotifications();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<NotificationResponseDto>> getPendingNotifications() {
        log.info("Fetching pending notifications");
        List<NotificationResponseDto> response = notificationService.getPendingNotifications();
        return ResponseEntity.ok(response);
    }
    
    // Failed notifications
    @GetMapping("/failed")
    public ResponseEntity<List<NotificationResponseDto>> getFailedNotifications() {
        log.info("Fetching failed notifications");
        List<NotificationResponseDto> response = notificationService.getFailedNotifications();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/failed/retry")
    public ResponseEntity<List<NotificationResponseDto>> getFailedNotificationsForRetry() {
        log.info("Fetching failed notifications for retry");
        List<NotificationResponseDto> response = notificationService.getFailedNotificationsForRetry();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/failed/date-range")
    public ResponseEntity<List<NotificationResponseDto>> getFailedNotificationsByDateRange(
            @RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        log.info("Fetching failed notifications between {} and {}", startDate, endDate);
        List<NotificationResponseDto> response = notificationService.getFailedNotificationsByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }
    
    // Expired notifications
    @GetMapping("/expired")
    public ResponseEntity<List<NotificationResponseDto>> getExpiredNotifications() {
        log.info("Fetching expired notifications");
        List<NotificationResponseDto> response = notificationService.getExpiredNotifications();
        return ResponseEntity.ok(response);
    }
    
    // Statistics operations
    @GetMapping("/stats/status/{status}")
    public ResponseEntity<Long> getNotificationCountByStatus(@PathVariable NotificationStatus status) {
        log.info("Getting notification count for status: {}", status);
        long count = notificationService.getNotificationCountByStatus(status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/type/{type}/status/{status}")
    public ResponseEntity<Long> getNotificationCountByTypeAndStatus(@PathVariable NotificationType type, @PathVariable NotificationStatus status) {
        log.info("Getting notification count for type: {} and status: {}", type, status);
        long count = notificationService.getNotificationCountByTypeAndStatus(type, status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/recipient/{recipient}/status/{status}")
    public ResponseEntity<Long> getNotificationCountByRecipientAndStatus(@PathVariable String recipient, @PathVariable NotificationStatus status) {
        log.info("Getting notification count for recipient: {} and status: {}", recipient, status);
        long count = notificationService.getNotificationCountByRecipientAndStatus(recipient, status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/event/{eventType}/status/{status}")
    public ResponseEntity<Long> getNotificationCountByEventTypeAndStatus(@PathVariable EventType eventType, @PathVariable NotificationStatus status) {
        log.info("Getting notification count for event type: {} and status: {}", eventType, status);
        long count = notificationService.getNotificationCountByEventTypeAndStatus(eventType, status);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/stats/date-range")
    public ResponseEntity<Long> getNotificationCountByDateRange(@RequestParam LocalDateTime startDate, @RequestParam LocalDateTime endDate) {
        log.info("Getting notification count between {} and {}", startDate, endDate);
        long count = notificationService.getNotificationCountByDateRange(startDate, endDate);
        return ResponseEntity.ok(count);
    }
    
    // Recent operations
    @GetMapping("/recent")
    public ResponseEntity<Page<NotificationListResponseDto>> getRecentNotifications(Pageable pageable) {
        log.info("Fetching recent notifications with pagination");
        Page<NotificationListResponseDto> response = notificationService.getRecentNotifications(pageable);
        return ResponseEntity.ok(response);
    }
    
    // Template operations
    @GetMapping("/template/{templateId}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByTemplate(@PathVariable String templateId) {
        log.info("Fetching notifications for template: {}", templateId);
        List<NotificationResponseDto> response = notificationService.getNotificationsByTemplate(templateId);
        return ResponseEntity.ok(response);
    }
    
    // External ID operations
    @GetMapping("/external-id/{pattern}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByExternalIdPattern(@PathVariable String pattern) {
        log.info("Fetching notifications for external ID pattern: {}", pattern);
        List<NotificationResponseDto> response = notificationService.getNotificationsByExternalIdPattern(pattern);
        return ResponseEntity.ok(response);
    }
    
    // Event-driven operations
    @PostMapping("/event/booking")
    public ResponseEntity<Void> handleBookingEvent(@RequestParam String eventType, @RequestBody Object bookingData) {
        log.info("Handling booking event: {}", eventType);
        notificationService.handleBookingEvent(eventType, bookingData);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/event/payment")
    public ResponseEntity<Void> handlePaymentEvent(@RequestParam String eventType, @RequestBody Object paymentData) {
        log.info("Handling payment event: {}", eventType);
        notificationService.handlePaymentEvent(eventType, paymentData);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/event/room")
    public ResponseEntity<Void> handleRoomEvent(@RequestParam String eventType, @RequestBody Object roomData) {
        log.info("Handling room event: {}", eventType);
        notificationService.handleRoomEvent(eventType, roomData);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/event/system")
    public ResponseEntity<Void> handleSystemEvent(@RequestParam String eventType, @RequestBody Object systemData) {
        log.info("Handling system event: {}", eventType);
        notificationService.handleSystemEvent(eventType, systemData);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/event/guest")
    public ResponseEntity<Void> handleGuestEvent(@RequestParam String eventType, @RequestBody Object guestData) {
        log.info("Handling guest event: {}", eventType);
        notificationService.handleGuestEvent(eventType, guestData);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/event/maintenance")
    public ResponseEntity<Void> handleMaintenanceEvent(@RequestParam String eventType, @RequestBody Object maintenanceData) {
        log.info("Handling maintenance event: {}", eventType);
        notificationService.handleMaintenanceEvent(eventType, maintenanceData);
        return ResponseEntity.ok().build();
    }
    
    // Test endpoints
    @GetMapping("/test-slack")
    public ResponseEntity<String> testSlackConnection() {
        log.info("Testing Slack connection");
        try {
            // This will test the Slack connection
            boolean isConnected = notificationService.testSlackConnection();
            if (isConnected) {
                return ResponseEntity.ok("✅ Slack connection successful!");
            } else {
                return ResponseEntity.status(500).body("❌ Slack connection failed!");
            }
        } catch (Exception e) {
            log.error("Error testing Slack connection: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("❌ Error testing Slack connection: " + e.getMessage());
        }
    }
    
    @PostMapping("/test-slack-message")
    public ResponseEntity<String> testSlackMessage(@RequestParam String channel, @RequestParam String message) {
        log.info("Testing Slack message to channel: {}", channel);
        try {
            // This will send a test message to Slack
            String result = notificationService.sendTestSlackMessage(channel, message);
            return ResponseEntity.ok("✅ Test message sent: " + result);
        } catch (Exception e) {
            log.error("Error sending test Slack message: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("❌ Error sending test message: " + e.getMessage());
        }
    }
}
