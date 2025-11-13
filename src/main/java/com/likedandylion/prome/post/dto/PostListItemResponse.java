package com.likedandylion.prome.post.dto;

import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.post.entity.Status;
import com.likedandylion.prome.user.entity.User;
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

        User user = post.getUser();
        String authorName = (user != null) ? user.getNickname() : "알 수 없음";

        return new PostListItemResponse(
                post.getId(),
                post.getTitle(),
                authorName,
                post.getViewCount(),
                statusName,
                post.getCreatedAt()
        );
    }
}