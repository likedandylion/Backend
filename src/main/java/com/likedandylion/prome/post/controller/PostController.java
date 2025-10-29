package com.likedandylion.prome.post.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.post.dto.PostListItemResponse;
import com.likedandylion.prome.post.service.PostQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostQueryService postQueryService;

    @Operation(summary = "프롬프트 검색", description = "제목/프롬프트 내용에서 keyword로 부분 일치 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostListItemResponse>>> search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        List<PostListItemResponse> data = postQueryService.search(keyword, sort, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "프롬프트 검색 성공", data));
    }
}