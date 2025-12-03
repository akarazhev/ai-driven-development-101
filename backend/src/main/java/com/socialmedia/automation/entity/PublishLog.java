package com.socialmedia.automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "publishlog")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long postId;
    
    @Column(nullable = false)
    private String provider;
    
    private String providerAccountId;
    
    private String externalId;
    
    @Column(nullable = false)
    private String status;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}

