package com.likedandylion.prome.post.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.likedandylion.prome.post.entity.Post;

public record PostListItemResponse(
        @JsonProperty("postId") Long postId,
        String title,
        String author,
        @JsonProperty("likes") long likes,
        @JsonProperty("views") int views
) {
    public static PostListItemResponse from(Post post, long likes) {
        String authorName = post.getUser() != null ? post.getUser().getNickname() : "익명";
        return new PostListItemResponse(
                post.getId(),
                post.getTitle(),
                authorName,
                likes,
                post.getViews()
        );
    }
}