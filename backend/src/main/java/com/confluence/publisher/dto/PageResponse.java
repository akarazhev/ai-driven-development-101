package com.confluence.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse {
    
    private Long id;
    private String title;
    private String content;
    private String spaceKey;
    private Long parentPageId;
    @Builder.Default
    private List<AttachmentInfo> attachments = new ArrayList<>();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentInfo {
        private Long id;
        private String filename;
        private String description;
    }
}

