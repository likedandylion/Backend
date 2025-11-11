package com.likedandylion.prome.bookmark.controller;

import com.likedandylion.prome.bookmark.dto.BookmarkToggleResponse;
import com.likedandylion.prome.bookmark.dto.MyBookmarkResponse;
import com.likedandylion.prome.bookmark.service.BookmarkService;
import com.likedandylion.prome.global.security.CustomUserDetails;
import com.likedandylion.prome.global.wrapper.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "bookmark-controller", description = "북마크 관련 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(
            summary = "북마크 추가/삭제",
            description = "게시글에 북마크가 없으면 추가하고, 이미 있으면 삭제하는 토글 기능입니다."
    )
    @PostMapping("/posts/{postId}/bookmark")
    public ApiResponse<BookmarkToggleResponse> toggleBookmark(
            @Parameter(description = "북마크할 게시글 ID", example = "3")
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getId(); // ⚠️ getUserId()이면 수정 필요

        BookmarkToggleResponse result = bookmarkService.toggle(postId, userId);

        String message = result.isBookmarked()
                ? "북마크가 추가되었습니다."
                : "북마크가 해제되었습니다.";

        return ApiResponse.success(message, result);
    }

    @Operation(
            summary = "내 북마크 목록 조회",
            description = "로그인한 사용자가 북마크한 게시글 목록을 조회합니다."
    )
    @GetMapping("/users/me/bookmarks")
    public ApiResponse<List<MyBookmarkResponse>> getMyBookmarks(
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        Long userId = user.getId();
        List<MyBookmarkResponse> data = bookmarkService.getMyBookmarks(userId);
        return ApiResponse.success("내 북마크 목록 조회 성공", data);
    }
}