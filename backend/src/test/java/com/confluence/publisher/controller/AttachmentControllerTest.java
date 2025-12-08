package com.confluence.publisher.controller;

import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttachmentService attachmentService;

    @Test
    void shouldUploadAttachment() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "test content".getBytes()
        );

        Attachment attachment = Attachment.builder()
                .id(1L)
                .filename("test.pdf")
                .description("Test description")
                .build();

        when(attachmentService.uploadAttachment(any(), anyString())).thenReturn(attachment);

        // When & Then
        mockMvc.perform(multipart("/api/attachments")
                        .file(file)
                        .param("description", "Test description"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.filename").value("test.pdf"));
    }

    @Test
    void shouldUploadAttachmentWithoutDescription() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "test content".getBytes()
        );

        Attachment attachment = Attachment.builder()
                .id(1L)
                .filename("test.pdf")
                .build();

        when(attachmentService.uploadAttachment(any(), any())).thenReturn(attachment);

        // When & Then
        mockMvc.perform(multipart("/api/attachments")
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
