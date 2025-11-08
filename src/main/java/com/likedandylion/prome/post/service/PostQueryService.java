package com.likedandylion.prome.post.service;

import com.likedandylion.prome.post.dto.PostListItemResponse;
import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.entity.Status;
import com.likedandylion.prome.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;


    /** 프리미엄 프롬프트 목록 조회 */
    @Transactional(readOnly = true)
    public List<PostListItemResponse> getPremiumList(String sort, Pageable pageable) {

        String sortKey = (sort == null || sort.isBlank()) ? "latest" : sort.toLowerCase();

        Sort sortSpec = switch (sortKey) {
            case "views" -> Sort.by(Sort.Direction.DESC, "views");
            case "likes" -> Sort.by(Sort.Direction.DESC, "createdAt"); // likes 정렬은 추후 구현
            default      -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortSpec);

        Page<Post> page = postRepository.findByStatus(Status.PREMIUM, p);

        return page.stream()
                .map(post -> {
                    int likeCount =
                            (post.getReactions() == null) ? 0 : post.getReactions().size();
                    return PostListItemResponse.from(post, likeCount);
                })
                .toList();
    }
}