package com.socialmedia.automation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostCreateRequest {
    
    @NotBlank(message = "Text is required")
    private String text;
    
    @NotNull
    private List<Long> mediaIds = new ArrayList<>();
}

