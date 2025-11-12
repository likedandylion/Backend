package com.likedandylion.prome.reaction.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.reaction.dto.LikeResponseDto;
import com.likedandylion.prome.reaction.service.LikeQueryService;
import com.likedandylion.prome.reaction.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {
    private final LikeService likeService;
    private final LikeQueryService queryService;
    @PostMapping("/posts/{postId}/likes")
    public ApiResponse<LikeResponseDto> likePost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        likeService.likePost(userId, postId);

        LikeResponseDto dto = new LikeResponseDto(
                queryService.getPostLikeCount(postId)
        );

        return new ApiResponse<>(
                true,
                "SUCCESS",
                "게시글 좋아요 완료",
                dto
        );
    }

    /** 게시글 좋아요 취소 */
    @DeleteMapping("/posts/{postId}/likes")
    public ApiResponse<LikeResponseDto> unlikePost(
            @PathVariable Long postId,
            @RequestParam Long userId
    ) {
        likeService.unlikePost(userId, postId);

        LikeResponseDto dto = new LikeResponseDto(
                queryService.getPostLikeCount(postId)
        );

        return new ApiResponse<>(
                true,
                "SUCCESS",
                "게시글 좋아요 취소 완료",
                dto
        );
    }

    /** 댓글 좋아요 */
    @PostMapping("/comments/{commentId}/likes")
    public ApiResponse<LikeResponseDto> likeComment(
            @PathVariable Long commentId,
            @RequestParam Long userId
    ) {
        likeService.likeComment(userId, commentId);

        LikeResponseDto dto = new LikeResponseDto(
                queryService.getCommentLikeCount(commentId)
        );

        return new ApiResponse<>(
                true,
                "SUCCESS",
                "댓글 좋아요 완료",
                dto
        );
    }

    /** 댓글 좋아요 취소 */
    @DeleteMapping("/comments/{commentId}/likes")
    public ApiResponse<LikeResponseDto> unlikeComment(
            @PathVariable Long commentId,
            @RequestParam Long userId
    ) {
        likeService.unlikeComment(userId, commentId);

        LikeResponseDto dto = new LikeResponseDto(
                queryService.getCommentLikeCount(commentId)
        );

        return new ApiResponse<>(
                true,
                "SUCCESS",
                "댓글 좋아요 취소 완료",
                dto
        );
    }
}