package com.likedandylion.prome.bookmark.dto;

import com.likedandylion.prome.bookmark.entity.Bookmark;
import com.likedandylion.prome.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyBookmarkResponse {

    private Long postId;
    private String title;
    private String author;
    private int likes;
    private int views;

    public static MyBookmarkResponse from(Bookmark bookmark) {
        Post post = bookmark.getPost();

        return MyBookmarkResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .author(post.getUser().getNickname())
                .likes(post.getLikeCount())
                .views(post.getViewCount())
                .build();
    }
}