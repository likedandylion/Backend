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

    public List<PostListItemResponse> getList(String sort, Pageable pageable) {
        Sort sortSpec = switch (sort.toLowerCase()) {
            case "views" -> Sort.by(Sort.Direction.DESC, "views");
            default      -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortSpec);

        Page<Post> page = postRepository.findByStatus(Status.ACTIVE, p);

        return page.stream()
                .map(post -> PostListItemResponse.from(post, post.getReactions().size()))
                .toList();
    }
}