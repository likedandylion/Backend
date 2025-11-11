package com.likedandylion.prome.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostDeleteResponse {
    private Long postId;
    private String message;
}