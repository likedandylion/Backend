package com.likedandylion.prome.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "댓글 수정을 위한 요청 데이터")
public class CommentUpdateRequest {

    @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
    @Schema(description = "수정할 댓글 내용", requiredMode = Schema.RequiredMode.REQUIRED, example = "케로로, 이런 작전으로는 어림도 없다!")
    private String content;
}