package com.confluence.publisher.service;

import com.confluence.publisher.config.AppProperties;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.exception.ServiceException;
import com.confluence.publisher.exception.ValidationException;
import com.confluence.publisher.repository.AttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private AppProperties appProperties;

    @Mock
    private MultipartFile multipartFile;

    private AttachmentService attachmentService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        attachmentService = new AttachmentService(attachmentRepository, appProperties);
        lenient().when(appProperties.getAttachmentDir()).thenReturn(tempDir.toString());
    }

    @Test
    void shouldUploadAttachmentSuccessfully() throws IOException {
        // Given
        String originalFilename = "test.pdf";
        String description = "Test attachment";
        byte[] fileContent = "test content".getBytes();

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getSize()).thenReturn((long) fileContent.length);
        when(multipartFile.getBytes()).thenReturn(fileContent);

        Attachment savedAttachment = Attachment.builder()
                .id(1L)
                .filename(originalFilename)
                .description(description)
                .build();

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(savedAttachment);

        // When
        Attachment result = attachmentService.uploadAttachment(multipartFile, description);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(originalFilename, result.getFilename());
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenFileIsNull() {
        // When & Then
        assertThrows(ValidationException.class, () -> {
            attachmentService.uploadAttachment(null, "description");
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenFileIsEmpty() {
        // Given
        when(multipartFile.isEmpty()).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            attachmentService.uploadAttachment(multipartFile, "description");
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenFilenameIsNull() {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        // When & Then
        assertThrows(ValidationException.class, () -> {
            attachmentService.uploadAttachment(multipartFile, "description");
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenFileTooLarge() {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(11L * 1024 * 1024); // 11MB

        // When & Then
        assertThrows(ValidationException.class, () -> {
            attachmentService.uploadAttachment(multipartFile, "description");
        });
    }

    @Test
    void shouldThrowServiceExceptionWhenFileWriteFails() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(100L);
        when(multipartFile.getBytes()).thenThrow(new IOException("Write failed"));

        // When & Then
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            attachmentService.uploadAttachment(multipartFile, "description");
        });

        assertTrue(exception.getMessage().contains("Failed to upload attachment"));
        verify(attachmentRepository, never()).save(any());
    }
}
