package com.likedandylion.prome.user.repository;

import com.likedandylion.prome.user.entity.AdWatchLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AdWatchLogRepository extends JpaRepository<AdWatchLog, Long> {

    // 오늘 이후 유저 시청 횟수 카운트
    int countByUserIdAndWatchedAtAfter(Long userId, LocalDateTime startTime);
}