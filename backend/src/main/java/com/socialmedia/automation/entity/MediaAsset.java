package com.socialmedia.automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mediaasset")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaAsset {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String filename;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long size;
    
    @Column(nullable = false)
    private String storagePath;
    
    private String altText;
}

