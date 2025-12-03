package com.confluence.publisher.service;

import com.confluence.publisher.dto.PageResponse;
import com.confluence.publisher.entity.Attachment;
import com.confluence.publisher.entity.Page;
import com.confluence.publisher.entity.PageAttachment;
import com.confluence.publisher.repository.AttachmentRepository;
import com.confluence.publisher.repository.PageAttachmentRepository;
import com.confluence.publisher.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PageService {
    
    private final PageRepository postRepository;
    private final PageAttachmentRepository postMediaRepository;
    private final AttachmentRepository mediaAssetRepository;
    
    @Transactional
    public Page createPost(String text, List<Long> mediaIds) {
        Page post = Post.builder()
                .text(text)
                .build();
        post = postRepository.save(page);
        
        List<PostMedia> postMediaList = IntStream.range(0, mediaIds.size())
                .mapToObj(i -> PostMedia.builder()
                        .postId(page.getId())
                        .mediaId(mediaIds.get(i))
                        .position(i)
                        .build())
                .toList();
        
        postMediaRepository.saveAll(postMediaList);
        return post;
    }
    
    @Transactional(readOnly = true)
    public PageResponse getPost(Long postId) {
        Page post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        
        List<PostMedia> postMediaList = postMediaRepository.findByPostIdOrderByPosition(postId);
        List<PageResponse.MediaInfo> media = postMediaList.stream()
                .map(pm -> {
                    MediaAsset asset = mediaAssetRepository.findById(pm.getMediaId()).orElse(null);
                    if (asset != null) {
                        return PageResponse.MediaInfo.builder()
                                .id(asset.getId())
                                .filename(asset.getFilename())
                                .altText(asset.getAltText())
                                .build();
                    }
                    return null;
                })
                .filter(m -> m != null)
                .toList();
        
        return PageResponse.builder()
                .id(page.getId())
                .text(page.getText())
                .media(media)
                .build();
    }
}

