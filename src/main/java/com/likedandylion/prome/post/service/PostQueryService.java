package com.likedandylion.prome.post.service;

import com.likedandylion.prome.post.dto.PostDetailResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;


    @Transactional
    public PostDetailResponse getDetail(Long postId) {
        Post post = postRepository.findWithAllById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));


        post.increaseViews();


        return PostDetailResponse.from(post);
    }
}