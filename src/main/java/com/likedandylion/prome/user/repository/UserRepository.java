package com.likedandylion.prome.user.repository;

import com.likedandylion.prome.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 추가
import org.springframework.data.repository.query.Param; // 추가

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);

    // N+1 성능 문제
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.subscription WHERE u.loginId = :loginId")
    Optional<User> findByLoginIdWithSubscription(@Param("loginId") String loginId);
}