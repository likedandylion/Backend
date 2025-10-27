package com.likedandylion.prome.user.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.user.dto.DeleteMyPostsRequest;   // 삭제 요청 DTO
import com.likedandylion.prome.user.dto.DeleteMyPostsResponse;  // 삭제 응답 DTO
import com.likedandylion.prome.user.dto.PostSimpleResponse;
import com.likedandylion.prome.user.service.UserPostCommandService; // 삭제 서비스
import com.likedandylion.prome.user.service.UserPostQueryService;
import jakarta.validation.Valid; // 요청 검증용
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * "내가 쓴 글" 컨트롤러
 * - 조회:   GET    /api/v1/users/me/posts
 * - 선택삭제: DELETE /api/v1/users/me/posts
 */
@RestController
@RequiredArgsConstructor
public class UserMePostController {

    private final UserPostQueryService userPostQueryService;     // 조회용 서비스
    private final UserPostCommandService userPostCommandService; // 삭제용 서비스

    /**
     * 내가 쓴 글 목록 조회
     */
    @GetMapping("/api/v1/users/me/posts")
    public ResponseEntity<ApiResponse<List<PostSimpleResponse>>> getMyPosts(Authentication authentication) {
        Long userId = resolveUserId(authentication); // 인증 주체에서 userId 추출
        List<PostSimpleResponse> data = userPostQueryService.getMyPosts(userId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "OK",
                "내가 쓴 글 목록입니다.",
                data
        ));
    }

    /**
     * 내가 쓴 글 선택 삭제
     * - body 예시: { "postIds": [101, 102, 999] }
     *   - 존재하고 내 글인 것 → deletedIds
     *   - 존재하지 않거나 내 글이 아닌 것 → skippedIds
     */
    @DeleteMapping("/api/v1/users/me/posts")
    public ResponseEntity<ApiResponse<DeleteMyPostsResponse>> deleteMyPosts(
            Authentication authentication,
            @Valid @RequestBody DeleteMyPostsRequest req // @Valid로 postIds 비어있음 등 검증
    ) {
        Long userId = resolveUserId(authentication);
        DeleteMyPostsResponse data = userPostCommandService.deleteMyPosts(userId, req);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "OK",
                "선택한 게시글이 삭제되었습니다.",
                data
        ));
    }

    /**
     * 인증 주체에서 userId를 안전하게 꺼내는 유틸
     * - Long / String / UserDetails(username) 순서로 시도
     */
    private Long resolveUserId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("인증 정보가 없습니다. 로그인 후 이용해주세요.");
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof Long l) return l;

        if (principal instanceof String s) {
            try { return Long.parseLong(s); } catch (NumberFormatException ignored) {}
        }

        if (principal instanceof UserDetails ud) {
            try { return Long.parseLong(ud.getUsername()); } catch (NumberFormatException ignored) {}
        }

        throw new IllegalStateException("인증 주체에서 userId를 추출할 수 없습니다. 인증 주입 로직을 확인하세요.");
    }
}
