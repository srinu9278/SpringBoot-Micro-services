package com.hotel.notification_service.service;

import com.hotel.notification_service.dto.SlackMessageDto;
import com.hotel.notification_service.dto.SlackResponseDto;

public interface SlackService {
    
    /**
     * Send a simple text message to a Slack channel
     */
    SlackResponseDto sendMessage(String channel, String message);
    
    /**
     * Send a rich message with blocks to a Slack channel
     */
    SlackResponseDto sendRichMessage(String channel, SlackMessageDto slackMessage);
    
    /**
     * Send a notification about booking events
     */
    SlackResponseDto sendBookingNotification(String channel, String eventType, Object bookingData);
    
    /**
     * Send a notification about payment events
     */
    SlackResponseDto sendPaymentNotification(String channel, String eventType, Object paymentData);
    
    /**
     * Send a notification about room events
     */
    SlackResponseDto sendRoomNotification(String channel, String eventType, Object roomData);
    
    /**
     * Send a system alert to admin channels
     */
    SlackResponseDto sendSystemAlert(String channel, String alertType, String message, String severity);
    
    /**
     * Send a daily summary report
     */
    SlackResponseDto sendDailySummary(String channel, Object summaryData);
    
    /**
     * Send a guest arrival notification
     */
    SlackResponseDto sendGuestArrivalNotification(String channel, Object guestData);
    
    /**
     * Send a maintenance alert
     */
    SlackResponseDto sendMaintenanceAlert(String channel, Object maintenanceData);
    
    /**
     * Test Slack connection
     */
    boolean testConnection();
}


