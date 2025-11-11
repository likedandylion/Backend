package com.likedandylion.prome.post.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.post.dto.PostCreateRequest;
import com.likedandylion.prome.post.dto.PostCreateResponse;
import com.likedandylion.prome.post.service.PostCommandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostCommandService postCommandService;

    @Operation(summary = "프롬프트 작성", description = "하나의 게시글과 3종류 프롬프트(GPT/GEMINI/CLAUDE)를 함께 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>> create(
            @Valid @RequestBody PostCreateRequest req) {

        PostCreateResponse data = postCommandService.create(req);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", "프롬프트 작성 성공", data)
        );
    }
}