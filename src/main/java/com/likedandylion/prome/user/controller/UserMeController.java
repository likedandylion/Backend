package com.likedandylion.prome.user.controller;

import com.likedandylion.prome.global.wrapper.ApiResponse;
import com.likedandylion.prome.user.dto.UserMeResponse;
import com.likedandylion.prome.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*; // GetMapping, PutMapping, RequestMapping, RestController

import org.springframework.security.core.annotation.AuthenticationPrincipal; // 현재 로그인 유저 주입
import jakarta.validation.Valid; // @Valid 사용
import com.likedandylion.prome.user.dto.UpdateProfileRequest; // 1단계에서 만든 DTO
import com.likedandylion.prome.user.dto.UpdateProfileResponse; // 1단계에서 만든 DTO
import com.likedandylion.prome.global.security.CustomUserDetails; // principal 타입

import com.likedandylion.prome.user.dto.ChangePasswordRequest;
import com.likedandylion.prome.user.dto.ChangePasswordResponse;

/**
 * 컨트롤러 = 카운터
 * - 손님(클라이언트) 요청을 받고, 주방(Service)에 요리를 부탁하고, 결과를 공통 포장(ApiResponse)으로 반환.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserMeController {

    private final UserQueryService userQueryService;

    public UserMeController(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    /**
     * [GET] /api/v1/users/me
     * - 현재 로그인한 사용자의 정보를 조회한다.
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 프로필/티켓/구독 정보를 반환합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserMeResponse>> getMe(Authentication authentication) {
        String loginId = authentication.getName(); // Security가 넣어준 username(loginId)
        UserMeResponse data = userQueryService.getMeByLoginId(loginId);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "내 정보가 조회되었습니다.", data));
    }

    /**
     * [PUT] /api/v1/users/me/profile
     * - 닉네임과 프로필 이미지 URL을 수정한다.
     * - 클래스에 @RequestMapping("/api/v1/users")가 있으므로 여기서는 "상대경로"만 쓴다.
     */
    @Operation(summary = "내 프로필 수정", description = "닉네임과 프로필 이미지 URL을 수정합니다.")
    @PutMapping("/me/profile") // 최종 경로: /api/v1/users/me/profile
    public ResponseEntity<ApiResponse<UpdateProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails principal, // 로그인 사용자
            @Valid @RequestBody UpdateProfileRequest req          // 요청 DTO 검증
    ) {
        Long userId = principal.getId(); // 현재 로그인 유저의 PK
        UpdateProfileResponse data = userQueryService.updateMyProfile(userId, req);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "프로필 변경 완료", data));
    }

    /**
     * [PUT] /api/v1/users/me/password
     * - 현재 비밀번호 확인 후, 새 비밀번호로 변경
     * - 요청 예시:
     *   {
     *     "currentPassword": "keroropassword123",
     *     "newPassword": "newpassword456"
     *   }
     * - 응답(우리 규칙상 ApiResponse로 감싸기):
     *   {
     *     "success": true,
     *     "code": "OK",
     *     "message": "비밀번호가 성공적으로 변경되었습니다.",
     *     "data": { "status": "success", "message": "비밀번호가 성공적으로 변경되었습니다." }
     *   }
     */
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 검증한 뒤 새 비밀번호로 변경합니다.")
    @PutMapping("/me/password") // 최종 경로: /api/v1/users/me/password
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changeMyPassword(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        // 1) 현재 로그인한 사용자 PK
        Long userId = principal.getId();

        // 2) 서비스에 위임(현재 비번 검증 → 새 비번 인코딩 저장)
        ChangePasswordResponse data = userQueryService.changeMyPassword(userId, req);

        // 3) 공통 래퍼로 성공 응답
        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", "비밀번호가 성공적으로 변경되었습니다.", data)
        );
    }
}