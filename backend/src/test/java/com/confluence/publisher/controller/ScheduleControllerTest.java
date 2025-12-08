package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ScheduleCreateRequest;
import com.confluence.publisher.entity.Schedule;
import com.confluence.publisher.service.ScheduleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ScheduleController.class)
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScheduleService scheduleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateSchedule() throws Exception {
        // Given
        ScheduleCreateRequest request = new ScheduleCreateRequest();
        request.setPageId(1L);

        Schedule schedule = Schedule.builder()
                .id(1L)
                .pageId(1L)
                .status("queued")
                .scheduledAt(Instant.now())
                .build();

        when(scheduleService.createSchedule(anyLong(), any())).thenReturn(schedule);

        // When & Then
        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.pageId").value(1L))
                .andExpect(jsonPath("$.status").value("queued"));
    }

    @Test
    void shouldListSchedules() throws Exception {
        // Given
        List<Schedule> schedules = Arrays.asList(
                Schedule.builder().id(1L).pageId(100L).status("queued").build(),
                Schedule.builder().id(2L).pageId(200L).status("published").build()
        );

        when(scheduleService.listSchedules(100)).thenReturn(schedules);

        // When & Then
        mockMvc.perform(get("/api/schedules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void shouldGetScheduleById() throws Exception {
        // Given
        Long scheduleId = 1L;
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .pageId(100L)
                .status("queued")
                .build();

        when(scheduleService.getSchedule(scheduleId)).thenReturn(schedule);

        // When & Then
        mockMvc.perform(get("/api/schedules/{id}", scheduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scheduleId))
                .andExpect(jsonPath("$.status").value("queued"));
    }
}
