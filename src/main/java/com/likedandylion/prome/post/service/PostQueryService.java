package com.likedandylion.prome.post.service;

import com.likedandylion.prome.post.dto.PostListItemResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
        // 정렬 파라미터 없거나 latest → createdAt DESC
        if (sort == null || sort.isBlank() || sort.equalsIgnoreCase("latest")) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }

        // views → 조회수 DESC
        if (sort.equalsIgnoreCase("views")) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "views")
            );
        }

        // likes 정렬은 아직 컬럼/집계 없으면 일단 createdAt DESC 로 fallback
        if (sort.equalsIgnoreCase("likes")) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }

        // 그 외 알 수 없는 값도 기본 createdAt DESC
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }
}