package com.likedandylion.prome.subscription.repository;

import com.likedandylion.prome.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
