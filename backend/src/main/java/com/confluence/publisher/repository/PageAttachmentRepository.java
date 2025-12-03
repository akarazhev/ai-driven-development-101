package com.confluence.publisher.repository;

import com.confluence.publisher.entity.PageAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageAttachmentRepository extends JpaRepository<PageAttachment, Long> {
    
    @Query("SELECT pm FROM PageAttachment pm WHERE pm.pageId = :pageId ORDER BY pm.position")
    List<PageAttachment> findByPostIdOrderByPosition(@Param("pageId") Long pageId);
    
    void deleteByPostId(Long pageId);
}

