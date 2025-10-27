package com.likedandylion.prome.user.service;

import com.likedandylion.prome.subscription.entity.Subscription;
import com.likedandylion.prome.user.dto.SubscriptionStatusResponse;
import com.likedandylion.prome.user.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 내 구독 상태 조회 서비스 구현체
 */
@Service
@RequiredArgsConstructor
public class UserSubscriptionQueryServiceImpl implements UserSubscriptionQueryService {

    private final UserSubscriptionRepository userSubscriptionRepository;

    @Override
    public SubscriptionStatusResponse getMySubscriptionStatus(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 1️. 현재 유효한 구독 조회
        Subscription active = userSubscriptionRepository
                .findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(userId, now)
                .orElse(null);

        // 2️. 현재 구독이 없을 때 가장 최근 구독 이력 확인 (옵션)
        if (active == null) {
            active = userSubscriptionRepository
                    .findTopByUser_IdOrderByEndDateDesc(userId)
                    .orElse(null);
        }

        // 3️. DTO로 변환 (없으면 기본값 false/null)
        return SubscriptionStatusResponse.from(active);
    }
}
