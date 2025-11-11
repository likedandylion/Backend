package com.likedandylion.prome.post.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotNull(message = "작성자 ID는 필수입니다.")  // 로그인 붙이기 전 임시
    private Long userId;

    @NotBlank
    private String title;  // 게시글 제목

    @NotBlank
    private String category; // ex) "observation"

    @NotEmpty
    private List<String> tags; // ex) ["한별아씨", "귀여워요"]

    @NotNull
    private Map<String, String> prompts;
    // ex) { "chatgpt": "...", "gemini": "...", "claude": "..." }
}