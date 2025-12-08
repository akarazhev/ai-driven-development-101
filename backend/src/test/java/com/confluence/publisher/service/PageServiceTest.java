package com.confluence.publisher.service;

import com.confluence.publisher.entity.Page;
import com.confluence.publisher.exception.ResourceNotFoundException;
import com.confluence.publisher.exception.ValidationException;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageServiceTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private PageAttachmentRepository pageAttachmentRepository;

    @Mock
    private AttachmentRepository attachmentRepository;

    private PageService pageService;

    private Page savedPage;

    @BeforeEach
    void setUp() {
        pageService = new PageService(pageRepository, pageAttachmentRepository, attachmentRepository);

        savedPage = new Page();
        savedPage.setId(1L);
        savedPage.setTitle("Test Page");
        savedPage.setContent("Test Content");
        savedPage.setSpaceKey("DEV");
    }

    @Test
    void shouldCreatePage() {
        when(pageRepository.save(any(Page.class))).thenReturn(savedPage);

        Page page = pageService.createPage(
                "Test Page",
                "Test Content",
                "DEV",
                null,
                Collections.emptyList()
        );

        assertNotNull(page);
        assertEquals(1L, page.getId());
        assertEquals("Test Page", page.getTitle());
        verify(pageRepository, times(1)).save(any(Page.class));
    }

    @Test
    void shouldThrowExceptionWhenPageNotFound() {
        when(pageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            pageService.getPage(1L);
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenTitleIsNull() {
        assertThrows(ValidationException.class, () -> {
            pageService.createPage(null, "Content", "DEV", null, Collections.emptyList());
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenTitleIsEmpty() {
        assertThrows(ValidationException.class, () -> {
            pageService.createPage("", "Content", "DEV", null, Collections.emptyList());
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenContentIsNull() {
        assertThrows(ValidationException.class, () -> {
            pageService.createPage("Title", null, "DEV", null, Collections.emptyList());
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenSpaceKeyIsNull() {
        assertThrows(ValidationException.class, () -> {
            pageService.createPage("Title", "Content", null, null, Collections.emptyList());
        });
    }
}
