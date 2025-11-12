package com.likedandylion.prome.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "댓글 좋아요 처리 후 응답 데이터")
public class CommentLikeResponse {

    @Schema(description = "좋아요 처리 후 최종 좋아요 개수", example = "11")
    private int finalLikesCount;

    @Schema(description = "사용자의 현재 좋아요 상태 (true: 좋아요 누름, false: 좋아요 취소)", example = "true")
    private boolean isLiked; // true: LIKE, false: NOT_LIKE
}