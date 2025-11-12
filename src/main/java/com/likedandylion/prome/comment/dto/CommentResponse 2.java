package com.likedandylion.prome.comment.dto;

import com.likedandylion.prome.comment.entity.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "댓글 생성, 수정, 조회 시 공통으로 사용되는 응답 데이터")
public class CommentResponse {

    @Schema(description = "댓글의 고유 ID", example = "502")
    private Long commentId;

    @Schema(description = "댓글 내용", example = "대장님, 멋져요! 질투나 버릴지도!")
    private String content;

    @Schema(description = "작성자 닉네임", example = "타마마")
    private String authorNickname;

    @Schema(description = "작성일")
    private LocalDateTime createdAt;

    @Schema(description = "수정일")
    private LocalDateTime updatedAt;

    @Schema(description = "좋아요 개수", example = "10")
    private int likesCount;

    /**
     * Entity -> DTO 변환 메서드
     */
    public static CommentResponse from(Comment comment) {
        // Like 리스트 개수 계산
        int likes = comment.getReactions() != null ? comment.getReactions().size() : 0;

        return CommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorNickname(comment.getUser().getNickname()) // Fetch Join으로 N+1 문제 방지
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likesCount(likes)
                .build();
    }
}