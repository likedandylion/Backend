package com.likedandylion.prome.user.service;

import com.likedandylion.prome.user.dto.UserMeResponse;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likedandylion.prome.user.dto.UpdateProfileRequest;
import com.likedandylion.prome.user.dto.UpdateProfileResponse;

import com.likedandylion.prome.user.dto.ChangePasswordRequest;
import com.likedandylion.prome.user.dto.ChangePasswordResponse;

import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserQueryService(UserRepository userRepository,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserMeResponse getMeByLoginId(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. loginId=" + loginId));
        return UserMeResponse.from(user);
    }

    @Transactional
    public UpdateProfileResponse updateMyProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String newNickname = req.getNickname();
        if (newNickname != null && !newNickname.equals(user.getNickname())) {
            if (userRepository.existsByNickname(newNickname)) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
        }

        user.updateProfile(newNickname, req.getProfileImageUrl());
        return UpdateProfileResponse.success();
    }

    @Transactional
    public ChangePasswordResponse changeMyPassword(Long userId, ChangePasswordRequest req) {
        // 1) 내 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2) 현재 비밀번호 검증
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 3) 새 비밀번호가 기존과 동일하면 방지
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("새 비밀번호가 기존 비밀번호와 동일합니다.");
        }

        // 4) 새 비밀번호 인코딩 후 저장
        user.changePassword(passwordEncoder.encode(req.getNewPassword()));

        // 5) 성공 응답
        return ChangePasswordResponse.success();
    }
}