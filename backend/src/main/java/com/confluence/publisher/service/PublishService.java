package com.confluence.publisher.service;

import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.entity.PublishLog;
import com.confluence.publisher.provider.BaseProvider;
import com.confluence.publisher.provider.ProviderFactory;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import com.confluence.publisher.repository.PublishLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PublishService {
    
    private final PageRepository postRepository;
    private final PageAttachmentRepository postMediaRepository;
    private final AttachmentRepository mediaAssetRepository;
    private final PublishLogRepository publishLogRepository;
    private final ProviderFactory providerFactory;
    
    @Transactional
    public PublishLog publishPost(Long pageId) {
        Page post = postRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + pageId));
        
        List<PostMedia> postMediaList = postMediaRepository.findByPostIdOrderByPosition(pageId);
        List<String> mediaPaths = postMediaList.stream()
                .map(pm -> {
                    MediaAsset asset = mediaAssetRepository.findById(pm.getMediaId())
                            .orElse(null);
                    return asset != null ? asset.getStoragePath() : null;
                })
                .filter(path -> path != null)
                .collect(Collectors.toList());
        
        BaseProvider provider = providerFactory.getProvider();
        BaseProvider.ProviderResult result = provider.publish(post.getText(), mediaPaths);
        
        PublishLog log = PublishLog.builder()
                .pageId(pageId)
                .provider(providerFactory.getProviderName())
                .externalId(result.externalId())
                .status("posted")
                .message(result.message())
                .build();
        
        return publishLogRepository.save(log);
    }
}

