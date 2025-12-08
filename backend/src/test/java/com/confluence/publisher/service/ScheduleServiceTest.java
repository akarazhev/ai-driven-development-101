package com.confluence.publisher.service;

import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private Schedule testSchedule;

    @BeforeEach
    void setUp() {
        testSchedule = Schedule.builder()
                .id(1L)
                .pageId(100L)
                .scheduledAt(Instant.now())
                .status("queued")
                .attemptCount(0)
                .build();
    }

    @Test
    void shouldCreateScheduleWithScheduledTime() {
        // Given
        Long pageId = 100L;
        Instant scheduledAt = Instant.now().plusSeconds(3600);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

        // When
        Schedule result = scheduleService.createSchedule(pageId, scheduledAt);

        // Then
        assertNotNull(result);
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void shouldCreateScheduleWithCurrentTimeWhenScheduledAtIsNull() {
        // Given
        Long pageId = 100L;
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

        // When
        Schedule result = scheduleService.createSchedule(pageId, null);

        // Then
        assertNotNull(result);
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    @Test
    void shouldGetScheduleById() {
        // Given
        Long scheduleId = 1L;
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(testSchedule));

        // When
        Schedule result = scheduleService.getSchedule(scheduleId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(scheduleRepository, times(1)).findById(scheduleId);
    }

    @Test
    void shouldThrowExceptionWhenScheduleNotFound() {
        // Given
        Long scheduleId = 999L;
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            scheduleService.getSchedule(scheduleId);
        });

        assertTrue(exception.getMessage().contains("Schedule not found"));
    }

    @Test
    void shouldListSchedules() {
        // Given
        int limit = 10;
        List<Schedule> schedules = Arrays.asList(testSchedule);
        PageImpl<Schedule> page = new PageImpl<>(schedules);
        when(scheduleRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        List<Schedule> result = scheduleService.listSchedules(limit);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scheduleRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldFindQueuedSchedules() {
        // Given
        Instant now = Instant.now();
        List<Schedule> queuedSchedules = Arrays.asList(testSchedule);
        when(scheduleRepository.findQueuedSchedulesBefore(now)).thenReturn(queuedSchedules);

        // When
        List<Schedule> result = scheduleService.findQueuedSchedules(now);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(scheduleRepository, times(1)).findQueuedSchedulesBefore(now);
    }

    @Test
    void shouldUpdateScheduleStatus() {
        // Given
        String newStatus = "published";
        String error = null;
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

        // When
        scheduleService.updateScheduleStatus(testSchedule, newStatus, error);

        // Then
        assertEquals(newStatus, testSchedule.getStatus());
        assertEquals(1, testSchedule.getAttemptCount());
        verify(scheduleRepository, times(1)).save(testSchedule);
    }

    @Test
    void shouldIncrementAttemptCountOnUpdate() {
        // Given
        testSchedule.setAttemptCount(2);
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(testSchedule);

        // When
        scheduleService.updateScheduleStatus(testSchedule, "failed", "Error message");

        // Then
        assertEquals(3, testSchedule.getAttemptCount());
        assertEquals("Error message", testSchedule.getLastError());
    }
}

