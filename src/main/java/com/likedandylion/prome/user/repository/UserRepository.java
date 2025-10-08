package com.likedandylion.prome.user.repository;

import com.likedandylion.prome.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    boolean existsByNickname(String nickname);
}