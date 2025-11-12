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

    @Schema(description = "싫어요 개수", example = "1")
    private int dislikesCount;

    // Entity 객체를 DTO 객체로 변환하는 정적 팩토리 메서드
    public static CommentResponse from(Comment comment) {
        // N+1 문제를 방지하기 위해 Service단에서 Fetch Join으로 가져온 User 엔티티를 사용합니다.
        // Reaction 리스트에서 LIKE와 DISLIKE 개수를 계산합니다.
        int likes = (int) comment.getReactions().stream()
                .filter(reaction -> reaction.getType() == ReactionType.LIKE)
                .count();
        int dislikes = (int) comment.getReactions().stream()
                .filter(reaction -> reaction.getType() == ReactionType.DISLIKE)
                .count();

        return CommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorNickname(comment.getUser().getNickname()) // Fetch Join으로 N+1 문제 해결
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .likesCount(likes)
                .dislikesCount(dislikes)
                .build();
    }
}