package com.likedandylion.prome.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotBlank
    private String title;  // 게시글 제목

    @NotBlank
    private String category; // ex) "observation"

    @NotEmpty
    private List<String> tags; // ex) ["한별아씨", "귀여워요"]

    @Schema(
            description = "프롬프트 내용 (chatgpt / gemini / claude)",
            example = "{\"chatgpt\": \"GPT용 프롬프트\", \"gemini\": \"Gemini용\", \"claude\": \"Claude용\"}"
    )
    @NotNull
    private Map<String, String> prompts;
    // ex) { "chatgpt": "...", "gemini": "...", "claude": "..." }
}