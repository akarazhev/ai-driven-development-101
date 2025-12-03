package com.confluence.publisher.repository;

import com.confluence.publisher.entity.PageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageAttachmentRepository extends JpaRepository<PageAttachment, Long> {
    
    @Query("SELECT pa FROM PageAttachment pa WHERE pa.pageId = :pageId ORDER BY pa.position")
    List<PageAttachment> findByPageIdOrderByPosition(@Param("pageId") Long pageId);
    
    void deleteByPageId(Long pageId);
}

