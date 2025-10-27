package com.likedandylion.prome.user.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.user.dto.SubscriptionStatusResponse;
import com.likedandylion.prome.user.service.UserSubscriptionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * "내 구독 상태 조회" 컨트롤러
 * - GET /api/v1/users/me/subscription
 * - 현재 로그인 사용자의 구독 정보를 조회합니다.
 */
@RestController
@RequiredArgsConstructor
public class UserMeSubscriptionController {

    private final UserSubscriptionQueryService userSubscriptionQueryService;

    @GetMapping("/api/v1/users/me/subscription")
    public ResponseEntity<ApiResponse<SubscriptionStatusResponse>> getMySubscription(Authentication authentication) {
        Long userId = resolveUserId(authentication);

        SubscriptionStatusResponse data = userSubscriptionQueryService.getMySubscriptionStatus(userId);

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "OK",
                "내 구독 상태를 조회했습니다.",
                data
        ));
    }

    /**
     * 인증 정보에서 userId를 추출하는 도우미 메서드
     */
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
