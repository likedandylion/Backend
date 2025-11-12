package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.SubscriptionStatusResponse;

public interface UserSubscriptionQueryService {
    /**
     * 현재 로그인 사용자의 구독 상태를 조회합니다.
     * - 구독 중이면 isPremium=true, 종료일 표시
     * - 구독이 없으면 false/null 반환
     */
    SubscriptionStatusResponse getMySubscriptionStatus(Long userId);
}