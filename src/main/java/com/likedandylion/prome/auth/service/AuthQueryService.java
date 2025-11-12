package com.likedandylion.prome.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthQueryService {
    private final UserRepository userRepository;

    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(normalizeLoginId(loginId));
    }
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(normalizeNickname(nickname));
    }

    private String normalizeLoginId(String s) { return s == null ? null : s.trim().toLowerCase(); }
    private String normalizeNickname(String s) { return s == null ? null : s.trim(); }
}