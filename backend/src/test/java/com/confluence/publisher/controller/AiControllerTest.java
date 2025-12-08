package com.confluence.publisher.controller;

import com.confluence.publisher.dto.AttachmentDescriptionRequest;
import com.confluence.publisher.dto.AttachmentDescriptionResponse;
import com.confluence.publisher.dto.ContentImprovementRequest;
import com.confluence.publisher.dto.ContentImprovementResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiController.class)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldImproveContent() throws Exception {
        // Given
        ContentImprovementRequest request = new ContentImprovementRequest();
        request.setContent("This is a test content that needs improvement");

        // When & Then
        mockMvc.perform(post("/api/ai/improve-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestions").isArray())
                .andExpect(jsonPath("$.suggestions.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void shouldGenerateDescription() throws Exception {
        // Given
        AttachmentDescriptionRequest request = new AttachmentDescriptionRequest();
        request.setDescription("A test image");

        // When & Then
        mockMvc.perform(post("/api/ai/generate-description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void shouldHandleEmptyContent() throws Exception {
        // Given - empty content should fail validation since @NotBlank is required
        ContentImprovementRequest request = new ContentImprovementRequest();
        request.setContent("");

        // When & Then - expect 400 Bad Request due to validation
        mockMvc.perform(post("/api/ai/improve-content")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

