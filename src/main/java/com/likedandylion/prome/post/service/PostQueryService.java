package com.likedandylion.prome.post.service;

import com.likedandylion.prome.post.dto.PostListItemResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.entity.Status;
import com.likedandylion.prome.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;

    // (기존) 일반 목록 메서드가 여기 있을 거야

    /** 프리미엄 목록 조회 */
    public List<PostListItemResponse> getPremiumList(String sort, Pageable pageable) {
        // 1) 정렬 스펙 결정
        Sort sortSpec = switch (sort == null ? "latest" : sort.toLowerCase()) {
            case "views" -> Sort.by(Sort.Direction.DESC, "views");
            case "likes" -> Sort.by(Sort.Direction.DESC, "reactions.size"); // 엔티티로 집계 안되면 latest로 유지
            default      -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortSpec);

        // 2) 프리미엄 상태만 조회 (Status.PREMIUM 값은 프로젝트 enum에 맞게 조정)
        Page<Post> page = postRepository.findByStatus(Status.PREMIUM, p);

        // 3) DTO로 변환 (리스트)
        return page.stream()
                .map(post -> PostListItemResponse.from(
                        post,
                        post.getReactions() == null ? 0 : post.getReactions().size()
                ))
                .toList();
    }
}