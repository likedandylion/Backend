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
                // 여기서 getNickname() 부분은 네 User 엔티티에 맞게 변경!
                .author(post.getUser().getNickname())
                .likes(post.getReactions().size())  // 필요 없으면 이 줄 지워도 됨
                .views(post.getViews())
                .build();
    }
}