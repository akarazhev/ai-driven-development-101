package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContentImprovementRequest {
    
    @NotBlank(content = "Message is required")
    private String content;
}

