package com.hotel.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SlackMessageDto {
    
    private String text;
    private String channel;
    private String username;
    private String iconEmoji;
    private String iconUrl;
    private List<SlackBlock> blocks;
    private List<SlackAttachment> attachments;
    private Map<String, Object> metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class SlackBlock {
        private String type;
        private SlackText text;
        private List<SlackElement> elements;
        private Map<String, Object> fields;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class SlackText {
        private String type;
        private String text;
        private boolean emoji;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class SlackElement {
        private String type;
        private SlackText text;
        private String actionId;
        private String value;
        private String url;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class SlackAttachment {
        private String color;
        private String title;
        private String titleLink;
        private String text;
        private String fallback;
        private List<SlackField> fields;
        private String footer;
        private String footerIcon;
        private Long ts;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class SlackField {
        private String title;
        private String value;
        private boolean shortField;
    }
}


