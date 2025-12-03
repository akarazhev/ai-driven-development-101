package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long pageId;
    
    @Column(nullable = false)
    private Instant scheduledAt;
    
    @Column(nullable = false)
    @Builder.Default
    private String status = "queued";
    
    @Column(nullable = false)
    @Builder.Default
    private Integer attemptCount = 0;
    
    @Column(columnDefinition = "TEXT")
    private String lastError;
}

