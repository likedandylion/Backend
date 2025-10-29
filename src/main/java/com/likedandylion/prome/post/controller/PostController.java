package com.likedandylion.prome.post.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.post.dto.PostDetailResponse;
import com.likedandylion.prome.post.service.PostQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post Controller", description = "프롬프트 게시글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostQueryService postQueryService;

    @Operation(summary = "프롬프트 상세 조회", description = "프롬프트의 상세 정보를 조회합니다. (조회수 +1)")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getDetail(@PathVariable Long postId) {
        PostDetailResponse data = postQueryService.getDetail(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "프롬프트 상세 조회 성공", data));
    }
}