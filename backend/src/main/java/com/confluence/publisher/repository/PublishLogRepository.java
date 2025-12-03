package com.confluence.publisher.repository;

import com.confluence.publisher.entity.PublishLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishLogRepository extends JpaRepository<PublishLog, Long> {
}

