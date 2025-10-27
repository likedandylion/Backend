package com.likedandylion.prome.user.repository;

import com.likedandylion.prome.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findTopByUser_IdAndEndDateAfterOrderByEndDateDesc(Long userId, LocalDateTime now);

    Optional<Subscription> findTopByUser_IdOrderByEndDateDesc(Long userId);

    // 활성 구독의 endDate를 "지금"으로 당겨 취소 처리 (세터 없이 반영)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
           update Subscription s
              set s.endDate = :now
            where s.id = :subscriptionId
              and s.user.id = :userId
           """)
    int cancelBySettingEndDateNow(
            @Param("subscriptionId") Long subscriptionId,
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now
    );
}
