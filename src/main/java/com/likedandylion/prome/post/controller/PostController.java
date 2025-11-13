package com.likedandylion.prome.post.controller;

import com.likedandylion.prome.global.security.CustomUserDetails;
import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.post.dto.*;
import com.likedandylion.prome.post.service.PostCommandService;
import com.likedandylion.prome.post.service.PostQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "페이지 번호 (0부터 시작)", schema = @Schema(type = "integer", defaultValue = "0"))
            @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기", schema = @Schema(type = "integer", defaultValue = "20"))
            @RequestParam(required = false) Integer size
    ) {
        List<PostListItemResponse> data = postQueryService.search(keyword, sort, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "프롬프트 검색 성공", data));
    }

    @Operation(summary = "프롬프트 작성", description = "하나의 게시글과 3종류 프롬프트(GPT/GEMINI/CLAUDE)를 함께 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>> create(
            @Valid @RequestBody PostCreateRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getId();
        PostCreateResponse data = postCommandService.create(userId, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "프롬프트 작성 성공", data));
    }

    @Operation(
            summary = "프롬프트/게시글 수정",
            description = "작성자 본인만 수정 가능합니다. title, status, prompts 중 전달된 값만 수정합니다."
    )
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostUpdateResponse>> update(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PostUpdateResponse data = postCommandService.update(userDetails.getId(), postId, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "수정 성공", data));
    }

    @Operation(summary = "게시글 전체 조회", description = "페이지네이션 기반 최신순 전체 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostListItemResponse>>> findAll(
            @RequestParam(required = false, defaultValue = "latest") String sort,
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable,
            @Parameter(description = "페이지 번호 (0부터 시작)", schema = @Schema(type = "integer", defaultValue = "0"))
            @RequestParam(required = false) Integer page,
            @Parameter(description = "페이지 크기", schema = @Schema(type = "integer", defaultValue = "20"))
            @RequestParam(required = false) Integer size
    ) {
        Page<PostListItemResponse> data = postQueryService.findAll(pageable, sort);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "전체 조회 성공", data));
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID 기반 상세 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> findById(
            @PathVariable Long postId
    ) {
        PostDetailResponse data = postQueryService.findById(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "상세 조회 성공", data));
    }
}