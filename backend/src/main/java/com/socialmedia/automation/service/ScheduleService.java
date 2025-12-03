package com.socialmedia.automation.service;

import com.socialmedia.automation.entity.Schedule;
import com.socialmedia.automation.repository.ScheduleRepository;
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
    public Schedule createSchedule(Long postId, Instant scheduledAt) {
        Instant when = scheduledAt != null ? scheduledAt : Instant.now();
        Schedule schedule = Schedule.builder()
                .postId(postId)
                .scheduledAt(when)
                .status("queued")
                .build();
        return scheduleRepository.save(schedule);
    }
    
    @Transactional(readOnly = true)
    public Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + scheduleId));
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

