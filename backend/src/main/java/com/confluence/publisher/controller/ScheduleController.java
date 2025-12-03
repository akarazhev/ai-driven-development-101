package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ScheduleCreateRequest;
import com.confluence.publisher.dto.ScheduleResponse;
import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleCreateRequest request) {
        Schedule schedule = scheduleService.createSchedule(
                request.getPageId(),
                request.getScheduledAt()
        );
        ScheduleResponse response = toResponse(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Long scheduleId) {
        Schedule schedule = scheduleService.getSchedule(scheduleId);
        return ResponseEntity.ok(toResponse(schedule));
    }
    
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> listSchedules() {
        List<Schedule> schedules = scheduleService.listSchedules(100);
        List<ScheduleResponse> responses = schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
    
    private ScheduleResponse toResponse(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .pageId(schedule.getPageId())
                .status(schedule.getStatus())
                .scheduledAt(schedule.getScheduledAt())
                .attemptCount(schedule.getAttemptCount())
                .lastError(schedule.getLastError())
                .build();
    }
}

