package com.socialmedia.automation.service;

import com.socialmedia.automation.dto.PostResponse;
import com.socialmedia.automation.entity.MediaAsset;
import com.socialmedia.automation.entity.Post;
import com.socialmedia.automation.entity.PostMedia;
import com.socialmedia.automation.repository.MediaAssetRepository;
import com.socialmedia.automation.repository.PostMediaRepository;
import com.socialmedia.automation.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final MediaAssetRepository mediaAssetRepository;
    
    @Transactional
    public Post createPost(String text, List<Long> mediaIds) {
        Post post = Post.builder()
                .text(text)
                .build();
        post = postRepository.save(post);
        
        List<PostMedia> postMediaList = IntStream.range(0, mediaIds.size())
                .mapToObj(i -> PostMedia.builder()
                        .postId(post.getId())
                        .mediaId(mediaIds.get(i))
                        .position(i)
                        .build())
                .toList();
        
        postMediaRepository.saveAll(postMediaList);
        return post;
    }
    
    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        
        List<PostMedia> postMediaList = postMediaRepository.findByPostIdOrderByPosition(postId);
        List<PostResponse.MediaInfo> media = postMediaList.stream()
                .map(pm -> {
                    MediaAsset asset = mediaAssetRepository.findById(pm.getMediaId()).orElse(null);
                    if (asset != null) {
                        return PostResponse.MediaInfo.builder()
                                .id(asset.getId())
                                .filename(asset.getFilename())
                                .altText(asset.getAltText())
                                .build();
                    }
                    return null;
                })
                .filter(m -> m != null)
                .toList();
        
        return PostResponse.builder()
                .id(post.getId())
                .text(post.getText())
                .media(media)
                .build();
    }
}

