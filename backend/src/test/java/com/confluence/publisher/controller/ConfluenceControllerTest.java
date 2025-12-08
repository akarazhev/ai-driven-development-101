package com.confluence.publisher.controller;

import com.confluence.publisher.dto.ConfluencePublishRequest;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.service.PublishService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConfluenceController.class)
class ConfluenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublishService publishService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishPage() throws Exception {
        // Given
        ConfluencePublishRequest request = new ConfluencePublishRequest();
        request.setPageId(1L);

        PublishLog publishLog = PublishLog.builder()
                .id(1L)
                .pageId(1L)
                .status("published")
                .confluencePageId("CONF-123")
                .build();

        when(publishService.publishPage(anyLong())).thenReturn(publishLog);

        // When & Then
        mockMvc.perform(post("/api/confluence/publish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("published"))
                .andExpect(jsonPath("$.confluencePageId").value("CONF-123"));
    }
}
