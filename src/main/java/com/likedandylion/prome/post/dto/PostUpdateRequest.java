package com.likedandylion.prome.post.dto;

import com.likedandylion.prome.post.entity.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @Schema(description = "수정할 제목", example = "수정된 제목입니다")
    @Size(max = 50)
    private String title;

    @Schema(description = "게시글 상태 (ACTIVE / INACTIVE)", example = "ACTIVE")
    private Status status;

    @Schema(
            description = "수정할 프롬프트 내용 (전달한 키값만 수정됨)",
            example = "{\"chatgpt\": \"수정할 GPT 프롬프트 내용\", \"gemini\": \"수정할 Gemini 내용\", \"claude\": \"수정할 claude 내용\"}"
    )
    private Map<String, String> prompts;
}