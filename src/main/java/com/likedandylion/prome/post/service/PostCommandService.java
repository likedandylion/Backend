package com.likedandylion.prome.post.service;

import com.likedandylion.prome.post.dto.PostDeleteResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCommandService {

    private final PostRepository postRepository;

    @Transactional
    public PostDeleteResponse delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        post.block(); // status -> BLOCKED

        return new PostDeleteResponse(post.getId(), "삭제(차단) 처리되었습니다.");
    }
}