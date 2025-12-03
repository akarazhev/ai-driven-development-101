package com.confluence.publisher.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pageattachment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageAttachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long pageId;
    
    @Column(nullable = false)
    private Long attachmentId;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;
}

