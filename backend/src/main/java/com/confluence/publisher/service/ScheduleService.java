package com.confluence.publisher.service;

import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.exception.ResourceNotFoundException;
import com.confluence.publisher.exception.ValidationException;
import com.confluence.publisher.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    
    @Transactional
    public Schedule createSchedule(Long pageId, Instant scheduledAt) {
        if (pageId == null) {
            throw new ValidationException("Page ID is required");
        }
        
        Instant when = scheduledAt != null ? scheduledAt : Instant.now();
        if (when.isBefore(Instant.now())) {
            throw new ValidationException("Scheduled time cannot be in the past");
        }
        
        Schedule schedule = Schedule.builder()
                .pageId(pageId)
                .scheduledAt(when)
                .status("queued")
                .build();
        return scheduleRepository.save(schedule);
    }
    
    @Transactional(readOnly = true)
    public Schedule getSchedule(Long scheduleId) {
        if (scheduleId == null) {
            throw new ValidationException("Schedule ID is required");
        }
        
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", scheduleId));
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> listSchedules(int limit) {
        return scheduleRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"))
        ).toList();
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> findQueuedSchedules(Instant now) {
        return scheduleRepository.findQueuedSchedulesBefore(now);
    }
    
    @Transactional
    public void updateScheduleStatus(Schedule schedule, String status, String error) {
        schedule.setStatus(status);
        schedule.setAttemptCount(schedule.getAttemptCount() + 1);
        schedule.setLastError(error);
        scheduleRepository.save(schedule);
    }
}

