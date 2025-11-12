package com.likedandylion.prome.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 생성을 위한 요청 데이터")
public class CommentCreateRequest {

    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    @Schema(description = "댓글 내용", requiredMode = Schema.RequiredMode.REQUIRED, example = "대장님, 멋져요! 질투나 버릴지도!")
    private String content;
}