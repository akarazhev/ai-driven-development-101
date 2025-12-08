package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageCreateRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    // Space key is optional - will use default from configuration if not provided
    private String spaceKey;
    
    private Long parentPageId;
    
    @NotNull
    private List<Long> attachmentIds = new ArrayList<>();
}

