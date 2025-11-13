package com.likedandylion.prome.user.dto;

import com.likedandylion.prome.post.entity.Post;
import com.likedandylion.prome.user.entity.User;

import java.time.Instant;
import java.time.ZoneOffset;

public record PostSimpleResponse(
        Long postId,
        String title,
        String authorName,
        Instant createdAt
) {
    public static PostSimpleResponse from(Post post) {
        Instant createdUtc = post.getCreatedAt() == null
                ? null
                : post.getCreatedAt().atOffset(ZoneOffset.UTC).toInstant();

        User user = post.getUser();
        String authorName = (user != null) ? user.getNickname() : "알 수 없음";

        return new PostSimpleResponse(
                post.getId(),
                post.getTitle(),
                authorName,
                createdUtc
        );
    }
}