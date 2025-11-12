package com.likedandylion.prome.user.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.user.dto.DeleteMyCommentsRequest;
import com.likedandylion.prome.user.dto.DeleteMyCommentsResponse;
import com.likedandylion.prome.user.dto.CommentSimpleResponse;
import com.likedandylion.prome.user.service.UserCommentCommandService;
import com.likedandylion.prome.user.service.UserCommentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * "내가 쓴 댓글" 관련 컨트롤러
 * - 조회(GET)
 * - 선택 삭제(DELETE)
 */
@RestController
@RequiredArgsConstructor
public class UserMeCommentController {

    private final UserCommentQueryService userCommentQueryService;
    private final UserCommentCommandService userCommentCommandService;

    /** 내가 쓴 댓글 목록 조회 */
    @GetMapping("/api/v1/users/me/comments")
    public ResponseEntity<ApiResponse<List<CommentSimpleResponse>>> getMyComments(Authentication authentication) {
        Long userId = resolveUserId(authentication);
        List<CommentSimpleResponse> data = userCommentQueryService.getMyComments(userId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "OK",
                "내가 쓴 댓글 목록입니다.",
                data
        ));
    }

    /** 내가 쓴 댓글 중 선택 삭제 */
    @DeleteMapping("/api/v1/users/me/comments")
    public ResponseEntity<ApiResponse<DeleteMyCommentsResponse>> deleteMyComments(
            Authentication authentication,
            @Validated @RequestBody DeleteMyCommentsRequest request
    ) {
        Long userId = resolveUserId(authentication);
        DeleteMyCommentsResponse data = userCommentCommandService.deleteMyComments(userId, request);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "OK",
                "선택한 댓글이 삭제되었습니다.",
                data
        ));
    }

    /** 공통: 인증 객체에서 userId 추출 */
    private Long resolveUserId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("인증 정보가 없습니다. 로그인 후 이용해주세요.");
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long l) return l;
        if (principal instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException ignored) {}
        }
        if (principal instanceof UserDetails ud) {
            try {
                return Long.parseLong(ud.getUsername());
            } catch (NumberFormatException ignored) {}
        }

        throw new IllegalStateException("인증 주체에서 userId를 추출할 수 없습니다.");
    }
}