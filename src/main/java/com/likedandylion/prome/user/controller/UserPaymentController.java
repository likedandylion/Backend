package com.likedandylion.prome.user.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.user.dto.PaymentCancelRequest;
import com.likedandylion.prome.user.dto.PaymentCancelResponse;
import com.likedandylion.prome.user.service.UserSubscriptionCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 결제/구독 관련 액션 컨트롤러
 * - 구독 취소: POST /api/v1/payments/cancel
 *
 * 왜 POST?
 * - 데이터 삭제(DELETE)가 아니라 "취소"라는 비즈니스 액션을 서버에 수행시키는 것이므로
 *   업계 표준(토스/카카오/Stripe 등)처럼 POST 엔드포인트를 사용합니다.
 */
@RestController
@RequiredArgsConstructor
public class UserPaymentController {

    private final UserSubscriptionCommandService userSubscriptionCommandService;

    /**
     * 구독 취소(결제 취소에 준하는 동작)
     * 요청 바디 예시:
     * {
     *   "paymentKey": "pay_123456",
     *   "reason": "사용하지 않음"
     * }
     */
    @PostMapping("/api/v1/payments/cancel")
    public ResponseEntity<ApiResponse<PaymentCancelResponse>> cancelSubscription(
            Authentication authentication,
            @Valid @RequestBody PaymentCancelRequest request
    ) {
        Long userId = resolveUserId(authentication);

        PaymentCancelResponse data = userSubscriptionCommandService.cancelMySubscription(userId, request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "OK",
                        data.canceled() ? "구독이 취소되었습니다." : "구독 취소에 실패했습니다.",
                        data
                )
        );
    }

    /**
     * 인증 주체에서 userId를 안전하게 추출
     * - 프로젝트 보안 설정에 따라 principal 타입이 다를 수 있어
     *   Long / String / UserDetails 순으로 시도합니다.
     */
    private Long resolveUserId(Authentication authentication) {
        if (authentication == null) {
            throw new IllegalStateException("인증 정보가 없습니다. 로그인 후 이용해주세요.");
        }
        Object principal = authentication.getPrincipal();

        // 1) principal이 Long인 경우
        if (principal instanceof Long l) return l;

        // 2) principal이 String인 경우(Long 파싱 시도)
        if (principal instanceof String s) {
            try { return Long.parseLong(s); } catch (NumberFormatException ignored) {}
        }

        // 3) principal이 UserDetails인 경우(username을 Long으로 사용한다고 가정)
        if (principal instanceof UserDetails ud) {
            try { return Long.parseLong(ud.getUsername()); } catch (NumberFormatException ignored) {}
        }

        throw new IllegalStateException("인증 주체에서 userId를 추출할 수 없습니다.");
    }
}
