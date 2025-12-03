package com.socialmedia.automation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PublishRequest {
    
    @NotNull(message = "Post ID is required")
    private Long postId;
}

