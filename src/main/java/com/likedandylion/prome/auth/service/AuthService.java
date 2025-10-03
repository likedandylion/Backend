package com.likedandylion.prome.auth.service;

import com.likedandylion.prome.auth.dto.LoginRequest;
import com.likedandylion.prome.auth.dto.LoginResponse;
import com.likedandylion.prome.auth.entity.RefreshToken;
import com.likedandylion.prome.auth.repository.RefreshTokenRepository;
import com.likedandylion.prome.auth.util.RefreshTokenUtil;
import com.likedandylion.prome.global.jwt.JwtProperties;
import com.likedandylion.prome.global.jwt.TokenProvider;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;
    private final JwtProperties jwtProperties;

    /** 로그인: Access 발급 + Refresh 저장(초기 발급) */
    @Transactional
    public LoginResponse login(LoginRequest request){
        // 1) 사용자 조회 & 비밀번호 검증
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 학번입니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 2) Access 토큰 발급
        String accessToken = tokenProvider.generateAccessToken(
                user.getLoginId(),
                Map.of("role", user.getRole().name())
        );

        // 3) Refresh 생성(원문) + 해시 저장
        String rawRefresh = refreshTokenUtil.generateRawToken(48);
        String hash = refreshTokenUtil.sha256Hex(rawRefresh);

        // 기존 행 있으면 삭제(또는 업데이트) 후 새로 저장
        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .tokenHash(hash)
                .expiresAt(Instant.now().plusSeconds(jwtProperties.getRefreshExpirySeconds()))
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(entity);

        return new LoginResponse(accessToken, rawRefresh);
    }
    @Transactional
    public LoginResponse refresh(String incomingRawRefresh) {
        if (incomingRawRefresh == null || incomingRawRefresh.isBlank()) {
            throw new IllegalArgumentException("refresh token required");
        }
        String hash = refreshTokenUtil.sha256Hex(incomingRawRefresh);

        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다."));

        if (current.isRevoked() || current.isExpired()) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        User user = current.getUser();

        // 새 토큰 발급
        String newAccess = tokenProvider.generateAccessToken(
                user.getLoginId(), Map.of("role", user.getRole().name())
        );
        String newRawRefresh = refreshTokenUtil.generateRawToken(48);
        String newHash = refreshTokenUtil.sha256Hex(newRawRefresh);

        // 교체(회전): 기존 레코드를 업데이트 하거나 삭제 후 새로 저장
        // 여기선 업데이트 방식
        current.rotateTo(newHash, Instant.now().plusSeconds(jwtProperties.getRefreshExpirySeconds()));
        // JPA 변경감지로 업데이트 반영

        return new LoginResponse(newAccess, newRawRefresh);
    }

    /** 로그아웃: 현재 리프레시 무효화 */
    @Transactional
    public void logout(String incomingRawRefresh) {
        if (incomingRawRefresh == null || incomingRawRefresh.isBlank()) return;
        String hash = refreshTokenUtil.sha256Hex(incomingRawRefresh);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(RefreshToken::revokeNow);
    }

}
