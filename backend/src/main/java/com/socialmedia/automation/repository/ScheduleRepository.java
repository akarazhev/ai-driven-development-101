package com.socialmedia.automation.repository;

import com.socialmedia.automation.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    @Query("SELECT s FROM Schedule s WHERE s.status = 'queued' AND s.scheduledAt <= :now ORDER BY s.scheduledAt")
    List<Schedule> findQueuedSchedulesBefore(@Param("now") Instant now);
}

