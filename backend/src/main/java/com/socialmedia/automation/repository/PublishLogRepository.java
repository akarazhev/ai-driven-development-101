package com.socialmedia.automation.repository;

import com.socialmedia.automation.entity.PublishLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishLogRepository extends JpaRepository<PublishLog, Long> {
}

