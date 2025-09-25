package com.hotel.notification_service.serviceImpl;

import com.hotel.notification_service.dto.SlackMessageDto;
import com.hotel.notification_service.dto.SlackResponseDto;
import com.hotel.notification_service.service.SlackService;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.auth.AuthTestRequest;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.auth.AuthTestResponse;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackServiceImpl implements SlackService {
    
    @Value("${slack.bot.token}")
    private String slackBotToken;
    
    @Value("${slack.default.channel}")
    private String defaultChannel;
    
    @Value("${slack.channels.bookings}")
    private String bookingsChannel;
    
    @Value("${slack.channels.payments}")
    private String paymentsChannel;
    
    @Value("${slack.channels.rooms}")
    private String roomsChannel;
    
    @Value("${slack.channels.guests}")
    private String guestsChannel;
    
    @Value("${slack.channels.maintenance}")
    private String maintenanceChannel;
    
    @Value("${slack.channels.alerts}")
    private String alertsChannel;
    
    @Value("${slack.channels.general}")
    private String generalChannel;
    
    private final Slack slack = Slack.getInstance();
    
    @Override
    public SlackResponseDto sendMessage(String channel, String message) {
        log.info("Sending Slack message to channel: {}", channel);
        
        // Check if Slack bot token is configured
        if (slackBotToken == null || slackBotToken.trim().isEmpty()) {
            log.warn("Slack bot token is not configured. Skipping Slack notification.");
            return SlackResponseDto.builder()
                    .success(false)
                    .error("Slack bot token not configured")
                    .channel(channel)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        
        try {
            MethodsClient methods = slack.methods(slackBotToken);
            
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(message)
                    .build();
            
            ChatPostMessageResponse response = methods.chatPostMessage(request);
            
            if (response.isOk()) {
                log.info("Slack message sent successfully. Message ID: {}", response.getTs());
                return SlackResponseDto.builder()
                        .success(true)
                        .messageId(response.getTs())
                        .channel(channel)
                        .timestamp(LocalDateTime.now())
                        .response(response.toString())
                        .build();
            } else {
                log.error("Failed to send Slack message: {}", response.getError());
                return SlackResponseDto.builder()
                        .success(false)
                        .error(response.getError())
                        .channel(channel)
                        .timestamp(LocalDateTime.now())
                        .build();
            }
            
        } catch (IOException | SlackApiException e) {
            log.error("Error sending Slack message to channel {}: {}", channel, e.getMessage(), e);
            return SlackResponseDto.builder()
                    .success(false)
                    .error(e.getMessage())
                    .channel(channel)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
    
    @Override
    public SlackResponseDto sendRichMessage(String channel, SlackMessageDto slackMessage) {
        log.info("Sending rich Slack message to channel: {}", channel);
        
        // Check if Slack bot token is configured
        if (slackBotToken == null || slackBotToken.trim().isEmpty()) {
            log.warn("Slack bot token is not configured. Skipping rich Slack notification.");
            return SlackResponseDto.builder()
                    .success(false)
                    .error("Slack bot token not configured")
                    .channel(channel)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
        
        try {
            MethodsClient methods = slack.methods(slackBotToken);
            
            ChatPostMessageRequest.ChatPostMessageRequestBuilder requestBuilder = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .text(slackMessage.getText());
            
            if (slackMessage.getUsername() != null) {
                requestBuilder.username(slackMessage.getUsername());
            }
            
            if (slackMessage.getIconEmoji() != null) {
                requestBuilder.iconEmoji(slackMessage.getIconEmoji());
            }
            
            if (slackMessage.getIconUrl() != null) {
                requestBuilder.iconUrl(slackMessage.getIconUrl());
            }
            
            if (slackMessage.getBlocks() != null && !slackMessage.getBlocks().isEmpty()) {
                // Convert custom blocks to Slack blocks
                List<com.slack.api.model.block.LayoutBlock> slackBlocks = convertToSlackBlocks(slackMessage.getBlocks());
                requestBuilder.blocks(slackBlocks);
            }
            
            ChatPostMessageResponse response = methods.chatPostMessage(requestBuilder.build());
            
            if (response.isOk()) {
                log.info("Rich Slack message sent successfully. Message ID: {}", response.getTs());
                return SlackResponseDto.builder()
                        .success(true)
                        .messageId(response.getTs())
                        .channel(channel)
                        .timestamp(LocalDateTime.now())
                        .response(response.toString())
                        .build();
            } else {
                log.error("Failed to send rich Slack message: {}", response.getError());
                return SlackResponseDto.builder()
                        .success(false)
                        .error(response.getError())
                        .channel(channel)
                        .timestamp(LocalDateTime.now())
                        .build();
            }
            
        } catch (IOException | SlackApiException e) {
            log.error("Error sending rich Slack message to channel {}: {}", channel, e.getMessage(), e);
            return SlackResponseDto.builder()
                    .success(false)
                    .error(e.getMessage())
                    .channel(channel)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }
    
    @Override
    public SlackResponseDto sendBookingNotification(String channel, String eventType, Object bookingData) {
        log.info("Sending booking notification to channel: {} for event: {}", channel, eventType);
        
        String message = String.format(":hotel: *Booking %s*\n\n%s", 
                eventType.toUpperCase(), 
                formatBookingData(bookingData));
        
        return sendMessage(channel, message);
    }
    
    @Override
    public SlackResponseDto sendPaymentNotification(String channel, String eventType, Object paymentData) {
        log.info("Sending payment notification to channel: {} for event: {}", channel, eventType);
        
        String message = String.format(":credit_card: *Payment %s*\n\n%s", 
                eventType.toUpperCase(), 
                formatPaymentData(paymentData));
        
        return sendMessage(channel, message);
    }
    
    @Override
    public SlackResponseDto sendRoomNotification(String channel, String eventType, Object roomData) {
        log.info("Sending room notification to channel: {} for event: {}", channel, eventType);
        
        String message = String.format(":door: *Room %s*\n\n%s", 
                eventType.toUpperCase(), 
                formatRoomData(roomData));
        
        return sendMessage(channel, message);
    }
    
    @Override
    public SlackResponseDto sendSystemAlert(String channel, String alertType, String message, String severity) {
        log.info("Sending system alert to channel: {} for alert: {}", channel, alertType);
        
        String emoji = getSeverityEmoji(severity);
        String alertMessage = String.format("%s *%s Alert*\n\n%s", 
                emoji, 
                alertType.toUpperCase(), 
                message);
        
        return sendMessage(channel, alertMessage);
    }
    
    @Override
    public SlackResponseDto sendDailySummary(String channel, Object summaryData) {
        log.info("Sending daily summary to channel: {}", channel);
        
        String message = String.format(":bar_chart: *Daily Hotel Summary*\n\n%s", 
                formatSummaryData(summaryData));
        
        return sendMessage(channel, message);
    }
    
    @Override
    public SlackResponseDto sendGuestArrivalNotification(String channel, Object guestData) {
        log.info("Sending guest arrival notification to channel: {}", channel);
        
        String message = String.format(":wave: *Guest Arrival*\n\n%s", 
                formatGuestData(guestData));
        
        return sendMessage(channel, message);
    }
    
    @Override
    public SlackResponseDto sendMaintenanceAlert(String channel, Object maintenanceData) {
        log.info("Sending maintenance alert to channel: {}", channel);
        
        String message = String.format(":wrench: *Maintenance Alert*\n\n%s", 
                formatMaintenanceData(maintenanceData));
        
        return sendMessage(channel, message);
    }
    
    @Override
    public boolean testConnection() {
        log.info("Testing Slack connection");
        
        // Check if Slack bot token is configured
        if (slackBotToken == null || slackBotToken.trim().isEmpty()) {
            log.warn("Slack bot token is not configured. Cannot test connection.");
            return false;
        }
        
        try {
            MethodsClient methods = slack.methods(slackBotToken);
            // Simple API call to test connection
            AuthTestRequest request = AuthTestRequest.builder().build();
            AuthTestResponse response = methods.authTest(request);
            boolean isOk = response.isOk();
            log.info("Slack connection test result: {}", isOk);
            return isOk;
        } catch (IOException | SlackApiException e) {
            log.error("Slack connection test failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    // Helper methods
    private List<com.slack.api.model.block.LayoutBlock> convertToSlackBlocks(List<SlackMessageDto.SlackBlock> blocks) {
        // Convert custom blocks to Slack API blocks
        // This is a simplified implementation
        return List.of();
    }
    
    private String formatBookingData(Object bookingData) {
        // Format booking data for Slack message
        return bookingData.toString();
    }
    
    private String formatPaymentData(Object paymentData) {
        // Format payment data for Slack message
        return paymentData.toString();
    }
    
    private String formatRoomData(Object roomData) {
        // Format room data for Slack message
        return roomData.toString();
    }
    
    private String formatSummaryData(Object summaryData) {
        // Format summary data for Slack message
        return summaryData.toString();
    }
    
    private String formatGuestData(Object guestData) {
        // Format guest data for Slack message
        return guestData.toString();
    }
    
    private String formatMaintenanceData(Object maintenanceData) {
        // Format maintenance data for Slack message
        return maintenanceData.toString();
    }
    
    private String getSeverityEmoji(String severity) {
        return switch (severity.toUpperCase()) {
            case "CRITICAL" -> ":red_circle:";
            case "HIGH" -> ":orange_circle:";
            case "MEDIUM" -> ":yellow_circle:";
            case "LOW" -> ":green_circle:";
            default -> ":white_circle:";
        };
    }
    
    // Channel getter methods
    public String getDefaultChannel() {
        return defaultChannel;
    }
    
    public String getBookingsChannel() {
        return bookingsChannel;
    }
    
    public String getPaymentsChannel() {
        return paymentsChannel;
    }
    
    public String getRoomsChannel() {
        return roomsChannel;
    }
    
    public String getGuestsChannel() {
        return guestsChannel;
    }
    
    public String getMaintenanceChannel() {
        return maintenanceChannel;
    }
    
    public String getAlertsChannel() {
        return alertsChannel;
    }
    
    public String getGeneralChannel() {
        return generalChannel;
    }
}

