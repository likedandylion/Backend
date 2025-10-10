package com.likedandylion.prome.post.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.post.dto.PostListItemResponse;
import com.likedandylion.prome.post.service.PostQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post Controller", description = "프롬프트 게시글 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostQueryService postQueryService;

    // (기존) GET /api/v1/posts 목록 조회

    @Operation(summary = "프리미엄 프롬프트 목록 조회",
            description = "정렬(latest/views/likes)에 따라 프리미엄 게시글만 조회합니다.")
    @GetMapping("/premium")
    public ResponseEntity<ApiResponse<List<PostListItemResponse>>> premiumList(
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        List<PostListItemResponse> data = postQueryService.getPremiumList(sort, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "프리미엄 프롬프트 목록 조회 성공", data));
    }
}