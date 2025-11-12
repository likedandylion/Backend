package com.likedandylion.prome.user.service;

import com.likedandylion.prome.subscription.entity.Subscription;
import com.likedandylion.prome.user.dto.PaymentCancelRequest;
import com.likedandylion.prome.user.dto.PaymentCancelResponse;
import com.likedandylion.prome.user.dto.SubscriptionStatusResponse;
import com.likedandylion.prome.user.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSubscriptionService {
    private final UserSubscriptionRepository userSubscriptionRepository;

    /**
     * [Query] 현재 로그인 사용자의 구독 상태 조회
     */
    @Transactional(readOnly = true)
    public SubscriptionStatusResponse getMySubscriptionStatus(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 현재 유효한 구독 조회
        Subscription active = userSubscriptionRepository
                .findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(userId, now)
                .orElse(null);

        // 2. 없으면 최근 종료된 구독 이력 조회
        if (active == null) {
            active = userSubscriptionRepository
                    .findTopByUser_IdOrderByEndDateDesc(userId)
                    .orElse(null);
        }

        // 3. DTO 변환
        return SubscriptionStatusResponse.from(active);
    }

    /**
     * [Command] 구독 취소 (비즈니스 액션)
     * - 활성 구독이 없으면 실패 응답
     * - 있으면 endDate를 현재 시각으로 업데이트
     */
    @Transactional
    public PaymentCancelResponse cancelMySubscription(Long userId, PaymentCancelRequest request) {
        LocalDateTime now = LocalDateTime.now();

        // 1. 활성 구독 확인
        Subscription active = userSubscriptionRepository
                .findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(userId, now)
                .orElse(null);

        if (active == null) {
            String note = "활성 구독이 없습니다. 이미 만료되었거나 결제가 존재하지 않습니다.";
            return PaymentCancelResponse.fail(request.getPaymentKey(), note);
        }

        // 2. endDate를 '지금'으로 업데이트 (JPQL 사용)
        int updated = userSubscriptionRepository.cancelBySettingEndDateNow(active.getId(), userId, now);
        if (updated < 1) {
            String note = "구독 취소 처리에 실패했습니다. 잠시 후 다시 시도해주세요.";
            return PaymentCancelResponse.fail(request.getPaymentKey(), note);
        }

        // 3. 성공 응답
        String note = "요청 사유: " + request.getReason();
        return PaymentCancelResponse.success(request.getPaymentKey(), note);
    }
}
