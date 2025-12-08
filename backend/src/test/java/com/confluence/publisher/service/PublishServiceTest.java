package com.confluence.publisher.service;

import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.exception.ResourceNotFoundException;
import com.confluence.publisher.provider.BaseProvider;
import com.confluence.publisher.provider.ProviderFactory;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import com.confluence.publisher.repository.PublishLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishServiceTest {

    @Mock
    private PageRepository pageRepository;

    @Mock
    private PageAttachmentRepository pageAttachmentRepository;

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private PublishLogRepository publishLogRepository;

    @Mock
    private ProviderFactory providerFactory;

    @Mock
    private BaseProvider provider;

    private PublishService publishService;

    private Page testPage;
    private Attachment testAttachment;

    @BeforeEach
    void setUp() {
        publishService = new PublishService(
                pageRepository,
                pageAttachmentRepository,
                attachmentRepository,
                publishLogRepository,
                providerFactory
        );

        testPage = Page.builder()
                .id(1L)
                .title("Test Page")
                .content("Test Content")
                .spaceKey("DEV")
                .build();

        testAttachment = Attachment.builder()
                .id(10L)
                .filename("test.pdf")
                .storagePath("/storage/attachments/test.pdf")
                .build();
    }

    @Test
    void shouldPublishPageSuccessfully() {
        // Given
        Long pageId = 1L;
        PageAttachment pageAttachment = PageAttachment.builder()
                .pageId(pageId)
                .attachmentId(10L)
                .position(0)
                .build();

        when(pageRepository.findById(pageId)).thenReturn(Optional.of(testPage));
        when(pageAttachmentRepository.findByPageIdOrderByPosition(pageId))
                .thenReturn(Arrays.asList(pageAttachment));
        when(attachmentRepository.findById(10L)).thenReturn(Optional.of(testAttachment));
        when(providerFactory.getProvider()).thenReturn(provider);
        when(providerFactory.getProviderName()).thenReturn("confluence-stub");

        BaseProvider.ProviderResult providerResult = new BaseProvider.ProviderResult(
                "CONF-123", "Published successfully"
        );
        when(provider.publishPage(anyString(), anyString(), anyString(), any(), anyList()))
                .thenReturn(providerResult);

        PublishLog savedLog = PublishLog.builder()
                .id(1L)
                .pageId(pageId)
                .status("published")
                .build();
        when(publishLogRepository.save(any(PublishLog.class))).thenReturn(savedLog);

        // When
        PublishLog result = publishService.publishPage(pageId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(pageRepository, times(1)).findById(pageId);
        verify(provider, times(1)).publishPage(anyString(), anyString(), anyString(), any(), anyList());
        verify(publishLogRepository, times(1)).save(any(PublishLog.class));
    }

    @Test
    void shouldThrowExceptionWhenPageNotFound() {
        // Given
        Long pageId = 999L;
        when(pageRepository.findById(pageId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            publishService.publishPage(pageId);
        });

        verify(providerFactory, never()).getProvider();
    }

    @Test
    void shouldPublishPageWithoutAttachments() {
        // Given
        Long pageId = 1L;
        when(pageRepository.findById(pageId)).thenReturn(Optional.of(testPage));
        when(pageAttachmentRepository.findByPageIdOrderByPosition(pageId))
                .thenReturn(Collections.emptyList());
        when(providerFactory.getProvider()).thenReturn(provider);
        when(providerFactory.getProviderName()).thenReturn("confluence-stub");

        BaseProvider.ProviderResult providerResult = new BaseProvider.ProviderResult(
                "CONF-123", "Published successfully"
        );
        when(provider.publishPage(anyString(), anyString(), anyString(), any(), anyList()))
                .thenReturn(providerResult);

        PublishLog savedLog = PublishLog.builder()
                .id(1L)
                .pageId(pageId)
                .status("published")
                .build();
        when(publishLogRepository.save(any(PublishLog.class))).thenReturn(savedLog);

        // When
        PublishLog result = publishService.publishPage(pageId);

        // Then
        assertNotNull(result);
        verify(provider, times(1)).publishPage(
                eq("DEV"),
                eq("Test Page"),
                eq("Test Content"),
                isNull(),
                eq(Collections.emptyList())
        );
    }
}
