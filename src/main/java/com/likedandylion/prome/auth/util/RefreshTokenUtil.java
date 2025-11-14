package com.likedandylion.prome.auth.util;

import com.likedandylion.prome.auth.entity.RefreshToken;
import com.likedandylion.prome.auth.repository.RefreshTokenRepository;
import com.likedandylion.prome.global.jwt.JwtProperties;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenUtil {

    private final SecureRandom secureRandom = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Transactional
    public String createAndSaveRefreshToken(String loginId) {

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + loginId));
        Long userId = user.getId();

        String rawToken = generateRawToken(32);
        String hashedToken = sha256Hex(rawToken);

        long expirySeconds = jwtProperties.getRefreshExpirySeconds();
        Instant expiresAt = Instant.now().plusSeconds(expirySeconds);

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserId(userId);

        if (existingTokenOpt.isPresent()) {
            // 이미 토큰이 존재하면, 새 값으로 교체 (UPDATE)
            RefreshToken existingToken = existingTokenOpt.get();
            existingToken.rotateTo(hashedToken, expiresAt);
            // @Transactional에 의해 자동 저장(dirty checking)
        } else {
            // 토큰이 없으면, 새로 생성 (INSERT)
            RefreshToken refreshToken = RefreshToken.builder()
                    .user(user)
                    .tokenHash(hashedToken)
                    .expiresAt(expiresAt)
                    .createdAt(Instant.now())
                    .revokedAt(null)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }

        return rawToken;
    }

    public String generateRawToken(int bytes) {
        byte[] b = new byte[bytes];
        secureRandom.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    public String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}