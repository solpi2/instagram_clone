package com.jinn.backend.service;

import com.jinn.backend.dto.PostRequest;
import com.jinn.backend.dto.PostResponse;
import com.jinn.backend.entity.Post;
import com.jinn.backend.entity.User;
import com.jinn.backend.exception.ResourceNotFoundException;
import com.jinn.backend.exception.UnauthorizedException;
import com.jinn.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final AuthenticationService authenticationService;

    public PostResponse createPost(PostRequest request) {
        User currentUser = authenticationService.getCurrentUser();

        Post post = Post.builder()
                .content(request.getContent())
                .user(currentUser)
                .deleted(false)
                .build();

        post = postRepository.save(post);
        return PostResponse.fromEntity(post);
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        authenticationService.getCurrentUser();
        Page<Post> posts = postRepository.findAllActive(pageable);
        return posts.map(PostResponse::fromEntity);
    }

    public PostResponse updatePost(Long postId, PostRequest request) {
        User currentUser = authenticationService.getCurrentUser();
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this post");
        }

        post.setContent(request.getContent());

        post = postRepository.save(post);
        return PostResponse.fromEntity(post);
    }

    public void deletePost(Long postId) {
        User currentUser = authenticationService.getCurrentUser();
        Post post = postRepository.findByIdAndNotDeleted(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this post");
        }

        post.setDeleted(true);
        postRepository.save(post);
    }
}