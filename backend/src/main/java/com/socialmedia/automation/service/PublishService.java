package com.socialmedia.automation.service;

import com.socialmedia.automation.entity.MediaAsset;
import com.socialmedia.automation.entity.Post;
import com.socialmedia.automation.entity.PostMedia;
import com.socialmedia.automation.entity.PublishLog;
import com.socialmedia.automation.provider.BaseProvider;
import com.socialmedia.automation.provider.ProviderFactory;
import com.socialmedia.automation.repository.MediaAssetRepository;
import com.socialmedia.automation.repository.PostMediaRepository;
import com.socialmedia.automation.repository.PostRepository;
import com.socialmedia.automation.repository.PublishLogRepository;
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
    
    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final MediaAssetRepository mediaAssetRepository;
    private final PublishLogRepository publishLogRepository;
    private final ProviderFactory providerFactory;
    
    @Transactional
    public PublishLog publishPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        
        List<PostMedia> postMediaList = postMediaRepository.findByPostIdOrderByPosition(postId);
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
                .postId(postId)
                .provider(providerFactory.getProviderName())
                .externalId(result.externalId())
                .status("posted")
                .message(result.message())
                .build();
        
        return publishLogRepository.save(log);
    }
}

