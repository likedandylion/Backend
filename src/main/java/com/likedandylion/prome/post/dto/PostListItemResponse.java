package com.likedandylion.prome.post.dto;

import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "프롬프트 목록/검색 응답 DTO")
public record PostListItemResponse(
        Long postId,
        String title,
        String authorName,
        int views,
        String status,
        LocalDateTime createdAt
) {
    public static PostListItemResponse from(Post post) {

        String statusName = (post.getStatus() != null) ? post.getStatus().name() : Status.ACTIVE.name();

        return new PostListItemResponse(
                post.getId(),
                post.getTitle(),
                post.getUser().getNickname(),
                post.getViewCount(),
                statusName,
                post.getCreatedAt()
        );
    }
}