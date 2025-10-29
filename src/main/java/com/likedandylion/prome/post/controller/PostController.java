package com.likedandylion.prome.post.controller;

import com.likedandylion.prome.global.dto.ApiResponse;
import com.likedandylion.prome.post.dto.PostUpdateRequest;
import com.likedandylion.prome.post.dto.PostUpdateResponse;
import com.likedandylion.prome.post.service.PostCommandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostCommandService postCommandService;

    @Operation(
            summary = "프롬프트/게시글 수정",
            description = "title, status, prompts(chatgpt/gemini/claude) 중 전달된 값만 수정합니다."
    )
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponse>> update(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest req
    ) {
        PostUpdateResponse data = postCommandService.update(postId, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "수정 성공", data));
    }
}