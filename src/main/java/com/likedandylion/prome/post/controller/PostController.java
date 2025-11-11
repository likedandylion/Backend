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
    private final PostCommandService postCommandService;

    @Operation(summary = "프롬프트 검색", description = "제목/프롬프트 내용에서 keyword로 부분 일치 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostListItemResponse>>> search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        List<PostListItemResponse> data = postQueryService.search(keyword, sort, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "프롬프트 검색 성공", data));
      
      private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;

    @Operation(summary = "프롬프트 작성", description = "하나의 게시글과 3종류 프롬프트(GPT/GEMINI/CLAUDE)를 함께 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>> create(
            @Valid @RequestBody PostCreateRequest req) {

        PostCreateResponse data = postCommandService.create(req);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", "프롬프트 작성 성공", data)
        );
    }

    @Operation(summary = "프롬프트 검색", description = "제목/프롬프트 내용에서 keyword로 부분 일치 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PostListItemResponse>>> search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        List<PostListItemResponse> data = postQueryService.search(keyword, sort, pageable);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", "프롬프트 검색 성공", data)
        );
    }
    
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
