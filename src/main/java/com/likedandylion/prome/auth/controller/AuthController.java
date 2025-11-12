package com.likedandylion.prome.auth.controller;

import com.likedandylion.prome.auth.dto.AvailabilityResponse;
import com.likedandylion.prome.auth.dto.LoginRequest;
import com.likedandylion.prome.auth.dto.LoginResponse;
import com.likedandylion.prome.auth.dto.SignupRequest;
import com.likedandylion.prome.auth.service.AuthQueryService;
import com.likedandylion.prome.auth.service.AuthService;
import com.likedandylion.prome.global.wrapper.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor; // 1. @RequiredArgsConstructor 임포트 추가
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 2. @RequestMapping, @RequestBody 등 임포트 추가

@Tag(name = "인증 API", description = "회원가입, 로그인, 중복 확인 API")
@RestController
@RequiredArgsConstructor // 3. final 필드를 초기화하는 생성자 자동 생성 (오류 21, 22 해결)
@RequestMapping("/api/v1/auth") // 4. 공통되는 API 경로 /api/v1/auth를 클래스 레벨로 추출
public class AuthController {

    private final AuthService authService;
    private final AuthQueryService authQueryService;

    @Operation(summary = "로그인 (AccessToken, RefreshToken 발급)")
    @PostMapping("/login")
    // 5. ApiResponse<T> 래퍼를 사용하도록 응답 타입 수정
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        // 6. ApiResponse.success() 정적 메서드를 사용하여 성공 응답 생성
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    // 7. SignupRequest 클래스명의 오타 수정 (SignupReques -> SignupRequest)
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest req) {
        authService.signup(req);
        // 8. 'status' has private access... 오류 해결을 위해 new ApiResponse() 사용
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "OK", "회원가입이 완료되었습니다.", null));
    }

    @Operation(summary = "ID 중복확인")
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> checkLoginId(
            @RequestParam String loginId) {

        boolean available = authQueryService.isLoginIdAvailable(loginId);
        AvailabilityResponse body = AvailabilityResponse.ofLoginId(available, loginId);

        // 9. 일관성을 위해 new ApiResponse() 사용
        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", null, body)
        );
    }

    @GetMapping("/check-nickname")
    @Operation(summary = "Nickname 중복확인")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> checkNickname(
            @RequestParam String nickname) {

        boolean available = authQueryService.isNicknameAvailable(nickname);
        AvailabilityResponse body = AvailabilityResponse.ofNickname(available, nickname);

        // 10. 일관성을 위해 new ApiResponse() 사용
        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", null, body)
        );
    }
}