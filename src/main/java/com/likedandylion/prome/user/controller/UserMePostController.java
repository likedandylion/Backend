package com.likedandylion.prome.user.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.user.dto.DeleteMyPostsRequest;
import com.likedandylion.prome.user.dto.DeleteMyPostsResponse;
import com.likedandylion.prome.user.dto.PostSimpleResponse;
import com.likedandylion.prome.global.security.CustomUserDetails;
import com.likedandylion.prome.user.service.UserContentCommandService;
import com.likedandylion.prome.user.service.UserContentQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserMePostController {

    private final UserContentQueryService userPostQueryService;
    private final UserContentCommandService userContentCommandService;

    @GetMapping("/api/v1/users/me/posts")
    public ResponseEntity<ApiResponse<List<PostSimpleResponse>>> getMyPosts(Authentication authentication) {
        Long userId = resolveUserId(authentication);
        List<PostSimpleResponse> data = userPostQueryService.getMyPosts(userId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "OK",
                "내가 쓴 글 목록입니다.",
                data
        ));
    }

    @DeleteMapping("/api/v1/users/me/posts")
    public ResponseEntity<ApiResponse<DeleteMyPostsResponse>> deleteMyPosts(
            Authentication authentication,
            @Valid @RequestBody DeleteMyPostsRequest req
    ) {
        Long userId = resolveUserId(authentication);
        DeleteMyPostsResponse data = userContentCommandService.deleteMyPosts(userId, req);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "OK",
                "선택한 게시글이 삭제되었습니다.",
                data
        ));
    }


    // 500 에러 해결
    private Long resolveUserId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("인증 정보가 없습니다. 로그인 후 이용해주세요.");
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        }

        if (principal instanceof Long l) return l;

        throw new IllegalStateException("인증 주체(" + principal.getClass().getName() + ")에서 Long 타입의 userId를 추출할 수 없습니다. CustomUserDetails를 사용하고 있는지 확인하세요.");
    }
}