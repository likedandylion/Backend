package com.likedandylion.prome.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostUpdateResponse {
    private Long postId;
    private String message;

    public static PostUpdateResponse of(Long postId, String message) {
        return new PostUpdateResponse(postId, message);
    }
}