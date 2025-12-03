package com.socialmedia.automation.controller;

import com.socialmedia.automation.dto.PostCreateRequest;
import com.socialmedia.automation.dto.PostResponse;
import com.socialmedia.automation.entity.Post;
import com.socialmedia.automation.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest request) {
        Post post = postService.createPost(request.getText(), request.getMediaIds());
        PostResponse response = PostResponse.builder()
                .id(post.getId())
                .text(post.getText())
                .media(List.of()) // Media will be loaded on get
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }
}

