package com.likedandylion.prome.post.controller;

import com.likedandylion.prome.global.dto.ApiResponse;
import com.likedandylion.prome.post.dto.PostDeleteResponse;
import com.likedandylion.prome.post.service.PostCommandService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostCommandService postCommandService;

    @Operation(summary = "프롬프트/게시글 삭제", description = "해당 게시글을 소프트 삭제(BLOCKED)로 처리합니다.")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDeleteResponse>> delete(@PathVariable Long postId) {
        PostDeleteResponse data = postCommandService.delete(postId);
        return ResponseEntity.ok(ApiResponse.ok("삭제 성공", data));
    }
}