package com.likedandylion.prome.bookmark.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkToggleResponse {

    private Long postId;
    private boolean bookmarked; // true = 북마크 추가, false = 북마크 해제
}