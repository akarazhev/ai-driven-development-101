package com.confluence.publisher.controller;

import com.confluence.publisher.dto.PageCreateRequest;
import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.exception.ResourceNotFoundException;
import com.confluence.publisher.service.PageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PageController.class)
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PageService pageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreatePage() throws Exception {
        // Given
        PageCreateRequest request = new PageCreateRequest();
        request.setTitle("Test Page");
        request.setContent("Test Content");
        request.setSpaceKey("DEV");
        request.setAttachmentIds(Collections.emptyList());

        Page createdPage = Page.builder()
                .id(1L)
                .title("Test Page")
                .content("Test Content")
                .spaceKey("DEV")
                .build();

        when(pageService.createPage(anyString(), anyString(), anyString(), any(), anyList()))
                .thenReturn(createdPage);

        // When & Then
        mockMvc.perform(post("/api/pages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Page"));
    }

    @Test
    void shouldGetPageById() throws Exception {
        // Given
        Long pageId = 1L;
        PageResponse response = PageResponse.builder()
                .id(pageId)
                .title("Test Page")
                .content("Test Content")
                .spaceKey("DEV")
                .attachments(Collections.emptyList())
                .build();

        when(pageService.getPage(pageId)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/pages/{id}", pageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pageId))
                .andExpect(jsonPath("$.title").value("Test Page"));
    }

    @Test
    void shouldReturnNotFoundWhenPageDoesNotExist() throws Exception {
        // Given
        Long pageId = 999L;
        when(pageService.getPage(pageId))
                .thenThrow(new ResourceNotFoundException("Page", pageId));

        // When & Then
        mockMvc.perform(get("/api/pages/{id}", pageId))
                .andExpect(status().isNotFound());
    }
}
