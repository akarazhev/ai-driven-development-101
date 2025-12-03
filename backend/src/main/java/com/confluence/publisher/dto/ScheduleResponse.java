package com.confluence.publisher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {
    
    private Long id;
    private Long pageId;
    private String status;
    private Instant scheduledAt;
    private Integer attemptCount;
    private String lastError;
}

