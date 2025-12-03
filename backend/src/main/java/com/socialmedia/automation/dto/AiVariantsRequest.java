package com.socialmedia.automation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiVariantsRequest {
    
    @NotBlank(message = "Message is required")
    private String message;
}

