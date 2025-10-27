package com.likedandylion.prome.user.dto;

import com.likedandylion.prome.subscription.entity.Subscription;

import java.time.LocalDateTime;

/**
 * 내 구독 상태를 조회하는 응답 DTO
 * - 프리미엄 여부(isPremium)
 * - 구독 종료일(subscriptionEndDate)
 */
public record SubscriptionStatusResponse(
        boolean isPremium,
        LocalDateTime subscriptionEndDate
) {
    /**
     * Subscription 엔티티 → DTO 변환
     * 구독이 없으면 기본값(false, null) 반환
     */
    public static SubscriptionStatusResponse from(Subscription subscription) {
        if (subscription == null) {
            return new SubscriptionStatusResponse(false, null);
        }

        boolean active = false;
        LocalDateTime endDate = subscription.getEndDate();

        // endDate가 null이 아니고, 아직 안 지났으면 프리미엄 상태
        if (endDate != null && endDate.isAfter(LocalDateTime.now())) {
            active = true;
        }

        return new SubscriptionStatusResponse(active, endDate);
    }
}
