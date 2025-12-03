package com.confluence.publisher.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfluencePublishRequest {
    
    @NotNull(message = "Page ID is required")
    private Long pageId;
}

