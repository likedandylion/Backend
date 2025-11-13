package com.likedandylion.prome.auth.service;

import com.likedandylion.prome.auth.dto.LoginRequest;
import com.likedandylion.prome.auth.dto.LoginResponse;
import com.likedandylion.prome.auth.dto.SignupRequest;
import com.likedandylion.prome.auth.entity.RefreshToken;
import com.likedandylion.prome.auth.repository.RefreshTokenRepository;
import com.likedandylion.prome.auth.util.RefreshTokenUtil;
import com.likedandylion.prome.global.exception.BadRequestException;
import com.likedandylion.prome.global.exception.ConflictException;
import com.likedandylion.prome.global.exception.NotFoundException;
import com.likedandylion.prome.global.exception.UnauthorizedException;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;
    private final JwtProperties jwtProperties;

    @Transactional
    public LoginResponse login(LoginRequest request){
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(()-> new NotFoundException("NOT_FOUND_USER", "존재하지 않는 유저입니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenProvider.generateAccessToken(
                user.getLoginId(),
                Map.of("role", user.getRole().name())
        );

        String rawRefresh = refreshTokenUtil.generateRawToken(48);
        String hash = refreshTokenUtil.sha256Hex(rawRefresh);
        Instant expiresAt = Instant.now().plusSeconds(jwtProperties.getRefreshExpirySeconds());

        Optional<RefreshToken> existingTokenOpt = refreshTokenRepository.findByUserId(user.getId());

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
            throw new BadRequestException("REFRESH_REQUIRED", "리프레시 토큰이 필요합니다.");
        }
        String hash = refreshTokenUtil.sha256Hex(incomingRawRefresh);

        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("INVALID_REFRESH", "유효하지 않은 리프레시 토큰입니다."));

        if (current.isRevoked() || current.isExpired()) {
            throw new UnauthorizedException("EXPIRED_REFRESH", "리프레시 토큰이 만료되었거나 폐기되었습니다.");
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

    @Transactional
    public void logout(String incomingRawRefresh) {
        if (incomingRawRefresh == null || incomingRawRefresh.isBlank()) return;
        String hash = refreshTokenUtil.sha256Hex(incomingRawRefresh);
        refreshTokenRepository.findByTokenHash(hash).ifPresent(RefreshToken::revokeNow);
    }

    @Transactional
    public void signup(SignupRequest req){
        if (userRepository.existsByLoginId(req.getLoginId())){
            throw new ConflictException("DUPLICATE_LOGIN_ID", "이미 사용중인 아이디입니다.");
        }
        if (!req.getPassword().equals(req.getPasswordConfirm())){
            throw new BadRequestException("PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다.");
        }
        String encodedPw = passwordEncoder.encode(req.getPassword());

        User u = User.initialUser(req.getLoginId(), encodedPw, req.getNickname());
        userRepository.save(u);
    }
}