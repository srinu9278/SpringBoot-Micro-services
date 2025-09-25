package com.hotel.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SlackResponseDto {
    
    private boolean success;
    private String messageId;
    private String channel;
    private String error;
    private LocalDateTime timestamp;
    private String response;
}

