package com.likedandylion.prome.auth.service;

import com.likedandylion.prome.auth.dto.LoginRequest;
import com.likedandylion.prome.auth.dto.LoginResponse;
import com.likedandylion.prome.auth.dto.SignupRequest;
import com.likedandylion.prome.auth.entity.RefreshToken;
import com.likedandylion.prome.auth.repository.RefreshTokenRepository;
import com.likedandylion.prome.auth.util.RefreshTokenUtil;
import com.likedandylion.prome.global.jwt.JwtProperties;
import com.likedandylion.prome.global.jwt.TokenProvider;
import com.likedandylion.prome.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // 이제 이 import가 정상적으로 작동합니다.
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder; // 에러 해결
    private final TokenProvider tokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;
    private final JwtProperties jwtProperties;

    /** 로그인: Access 발급 + Refresh 저장(초기 발급) */
    @Transactional
    public LoginResponse login(LoginRequest request){
        // 1) 사용자 조회 & 비밀번호 검증
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 학번입니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) { // 에러 해결
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
        Instant expiresAt = Instant.now().plusSeconds(jwtProperties.getRefreshExpirySeconds());

        // 4. [로직 수정] 기존 토큰 조회 및 처리 (로그인 중복 오류 방지)
        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUser(user);

        if (existingTokenOpt.isPresent()) {
            RefreshToken existingToken = existingTokenOpt.get();
            existingToken.rotateTo(hash, expiresAt);
        } else {
            RefreshToken entity = RefreshToken.builder()
                    .user(user)
                    .tokenHash(hash)
                    .expiresAt(expiresAt)
                    .createdAt(Instant.now())
                    .build();
            refreshTokenRepository.save(entity);
        }

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

        String newAccess = tokenProvider.generateAccessToken(
                user.getLoginId(), Map.of("role", user.getRole().name())
        );
        String newRawRefresh = refreshTokenUtil.generateRawToken(48);
        String newHash = refreshTokenUtil.sha256Hex(newRawRefresh);

        current.rotateTo(newHash, Instant.now().plusSeconds(jwtProperties.getRefreshExpirySeconds()));

        return new LoginResponse(newAccess, newRawRefresh);
    }

    /** 로그아웃: 현재 리프레시 무효화 */
    @Transactional
    public void logout(String incomingRawRefresh) {
        if (incomingRawRefresh == null || incomingRawRefresh.isBlank()) return;
        String hash = refreshTokenUtil.sha256Hex(incomingRawRefresh);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(RefreshToken::revokeNow);
    }

    @Transactional
    public void signup(SignupRequest req){
        if (userRepository.existsByLoginId(req.getLoginId())){
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if (!req.getPassword().equals(req.getPasswordConfirm())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String encodedPw = passwordEncoder.encode(req.getPassword()); // 에러 해결

        // [오타 수정] 'initalUser' -> 'initialUser'
        User u = User.initialUser(req.getLoginId(), encodedPw, req.getNickname());
        userRepository.save(u);
    }
}