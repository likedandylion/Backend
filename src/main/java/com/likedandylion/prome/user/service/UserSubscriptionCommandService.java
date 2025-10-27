package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.PaymentCancelRequest;
import com.likedandylion.prome.user.dto.PaymentCancelResponse;

/**
 * 구독/결제 취소(비즈니스 액션) 커맨드 서비스
 * - POST /api/v1/payments/cancel 에서 호출될 예정
 */
public interface UserSubscriptionCommandService {

    /**
     * 현재 로그인 사용자의 활성 구독을 취소합니다.
     * - 활성 구독이 있으면 종료일을 "지금"으로 당겨 사실상의 취소 처리
     * - 활성 구독이 없으면 실패(또는 스킵) 응답
     */
    PaymentCancelResponse cancelMySubscription(Long userId, PaymentCancelRequest request);
}
