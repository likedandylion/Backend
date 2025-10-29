package com.likedandylion.prome.post.service;

import com.likedandylion.prome.post.dto.PostListItemResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;

    public List<PostListItemResponse> search(String keyword, String sort, Pageable pageable) {
        Pageable p = withSort(pageable, sort);
        Page<Post> page = postRepository.searchByKeyword(keyword, p);
        return page.stream()
                .map(PostListItemResponse::from)
                .toList();
    }

    private Pageable withSort(Pageable pageable, String sort) {
        if (sort == null || sort.isBlank() || sort.equalsIgnoreCase("latest")) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        if (sort.equalsIgnoreCase("views")) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "views"));
        }
        if (sort.equalsIgnoreCase("likes")) {
            // 좋아요 수 컬럼/집계가 따로 없으면 일단 createdAt 로 fallback 하거나, 반영 예정 주석
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}