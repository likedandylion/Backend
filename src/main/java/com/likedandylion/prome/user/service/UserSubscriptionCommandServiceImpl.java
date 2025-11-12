package com.likedandylion.prome.user.service;

import com.likedandylion.prome.subscription.entity.Subscription;
import com.likedandylion.prome.user.dto.PaymentCancelRequest;
import com.likedandylion.prome.user.dto.PaymentCancelResponse;
import com.likedandylion.prome.user.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSubscriptionCommandServiceImpl implements UserSubscriptionCommandService {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Override
    @Transactional
    public PaymentCancelResponse cancelMySubscription(Long userId, PaymentCancelRequest request) {
        LocalDateTime now = LocalDateTime.now();

        // 1) 현재 유효한 구독 찾기
        Subscription active = userSubscriptionRepository
                .findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(userId, now)
                .orElse(null);

        if (active == null) {
            String note = "활성 구독이 없습니다. 이미 만료되었거나 결제가 존재하지 않습니다.";
            return PaymentCancelResponse.fail(request.getPaymentKey(), note);
        }

        // 2) 세터 없이 JPQL 업데이트로 endDate를 '지금'으로 변경
        int updated = userSubscriptionRepository.cancelBySettingEndDateNow(active.getId(), userId, now);
        if (updated < 1) {
            // 동시성 등으로 업데이트가 반영되지 않은 예외 상황을 방어
            String note = "구독 취소 처리에 실패했습니다. 잠시 후 다시 시도해주세요.";
            return PaymentCancelResponse.fail(request.getPaymentKey(), note);
        }

        // 3) 성공 응답
        String note = "요청 사유: " + request.getReason();
        return PaymentCancelResponse.success(request.getPaymentKey(), note);
    }
}