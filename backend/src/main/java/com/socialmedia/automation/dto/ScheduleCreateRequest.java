package com.socialmedia.automation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class ScheduleCreateRequest {
    
    @NotNull(message = "Post ID is required")
    private Long postId;
    
    private Instant scheduledAt;
}

