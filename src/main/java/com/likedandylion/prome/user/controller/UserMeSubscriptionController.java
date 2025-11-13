package com.likedandylion.prome.user.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.global.security.CustomUserDetails;
import com.likedandylion.prome.user.dto.SubscriptionStatusResponse;
import com.likedandylion.prome.user.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserMeSubscriptionController {

    private final UserSubscriptionService userSubscriptionQueryService;

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

    // 500 에러 해결
    private Long resolveUserId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("인증 정보가 없습니다. 로그인 후 이용해주세요.");
        }
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            // getUserId()가 아닌 getId() 호출
            return ((CustomUserDetails) principal).getId();
        }

        // Principal이 Long 타입일 때
        if (principal instanceof Long l) return l;

        throw new IllegalStateException("인증 주체(" + principal.getClass().getName() + ")에서 Long 타입의 userId를 추출할 수 없습니다. CustomUserDetails를 사용하고 있는지 확인하세요.");
    }
}