package com.likedandylion.prome.auth.repository;

import com.likedandylion.prome.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(Long userId);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Transactional
    void deleteByUserId(Long userId);
}