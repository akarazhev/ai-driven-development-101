package com.socialmedia.automation.repository;

import com.socialmedia.automation.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    
    @Query("SELECT pm FROM PostMedia pm WHERE pm.postId = :postId ORDER BY pm.position")
    List<PostMedia> findByPostIdOrderByPosition(@Param("postId") Long postId);
    
    void deleteByPostId(Long postId);
}

