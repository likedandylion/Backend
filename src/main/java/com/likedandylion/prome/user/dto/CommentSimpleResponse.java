package com.likedandylion.prome.user.dto;

import com.likedandylion.prome.comment.entity.Comment;

import java.time.Instant;
import java.time.ZoneOffset;

/**
 * "내가 쓴 댓글" 목록의 단일 아이템 응답 DTO.
 * - Response DTO는 record (불변)
 * - createdAt은 예시처럼 UTC(Z) 포맷으로 내려가도록 Instant 사용
 */
public record CommentSimpleResponse(
        Long commentId,   // 댓글 ID
        String content,   // 댓글 내용(요약)
        Instant createdAt // 생성 시각(UTC, 예: 2025-09-29T10:00:00Z)
) {
    /**
     * 엔티티(Comment) -> DTO 변환.
     * Comment.createdAt이 LocalDateTime이라고 가정하고,
     * UTC Instant로 변환해줍니다.
     * (만약 Comment.createdAt이 이미 Instant라면 바로 사용해도 됩니다.)
     */
    public static CommentSimpleResponse from(Comment comment) {
        Instant createdUtc = null;
        if (comment.getCreatedAt() != null) {
            // LocalDateTime -> Instant (UTC)
            createdUtc = comment.getCreatedAt().atOffset(ZoneOffset.UTC).toInstant();
        }

        return new CommentSimpleResponse(
                comment.getId(),
                comment.getContent(),
                createdUtc
        );
    }
}
