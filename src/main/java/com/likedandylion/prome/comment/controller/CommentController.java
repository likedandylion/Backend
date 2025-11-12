package com.likedandylion.prome.comment.controller;

import com.likedandylion.prome.comment.dto.CommentCreateRequest;
import com.likedandylion.prome.comment.dto.CommentResponse;
import com.likedandylion.prome.comment.dto.CommentUpdateRequest;
import com.likedandylion.prome.comment.dto.CommentLikeResponse;
import com.likedandylion.prome.comment.service.CommentService;
import com.likedandylion.prome.global.security.CustomUserDetails;
import com.likedandylion.prome.global.wrapper.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "댓글 API", description = "댓글 CRUD 및 좋아요 관련 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth") // 이 컨트롤러의 모든 API는 인증이 필요함을 명시
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록 조회", description = "특정 게시글에 달린 모든 댓글을 조회합니다.")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(ApiResponse.success(commentService.getComments(postId)));
    }

    @Operation(summary = "댓글 작성", description = "특정 게시글에 새로운 댓글을 작성합니다. (로그인 필요)")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 현재 로그인한 사용자의 ID를 Service로 전달
        CommentResponse response = commentService.createComment(postId, request, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "댓글 수정", description = "자신이 작성한 댓글의 내용을 수정합니다. (로그인 필요)")
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 사용자 ID를 전달하여 권한 검사
        CommentResponse response = commentService.updateComment(commentId, request, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "댓글 삭제", description = "자신이 작성한 댓글을 삭제합니다. (로그인 필요)")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // 사용자 ID를 전달하여 권한 검사
        commentService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(null)); // 성공 시 200 OK와 빈 데이터 반환
    }

}