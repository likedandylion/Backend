package com.likedandylion.prome.user.dto;

import com.likedandylion.prome.post.entity.Post;

import java.time.Instant;
import java.time.ZoneOffset;

public record PostSimpleResponse(
        Long postId,
        String title,
        Instant createdAt
) {
    public static PostSimpleResponse from(Post post) {
        Instant createdUtc = post.getCreatedAt() == null
                ? null
                : post.getCreatedAt().atOffset(ZoneOffset.UTC).toInstant();

        return new PostSimpleResponse(
                post.getId(),
                post.getTitle(),
                createdUtc
        );
    }
}