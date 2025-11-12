package com.likedandylion.prome.post.service;

import com.likedandylion.prome.global.exception.BadRequestException;
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
        if (sort == null || sort.isBlank() || sort.equalsIgnoreCase("latest")) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }

        if (sort.equalsIgnoreCase("views")) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "views")
            );
        }

        if (sort.equalsIgnoreCase("likes")) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt")
            );
        }

        // 정의되지 않은 정렬 기준이면 명시적 예외 발생
        throw new BadRequestException("INVALID_SORT_TYPE", "지원하지 않는 정렬 기준입니다.");
    }
}