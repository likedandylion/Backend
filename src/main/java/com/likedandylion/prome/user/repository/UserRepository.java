package com.likedandylion.prome.user.repository;

import com.likedandylion.prome.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);

    // 닉네임 중복 체크용 (서비스에서 사용)
    boolean existsByNickname(String nickname);
}