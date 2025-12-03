package com.socialmedia.automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "postmedia")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostMedia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long postId;
    
    @Column(nullable = false)
    private Long mediaId;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;
}

