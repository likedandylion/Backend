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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthQueryService authQueryService;

    @Operation(summary = "로그인 (AccessToken, RefreshToken 발급)")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req){
        LoginResponse data = authService.login(req);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "로그인 되었습니다.", data));
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(@Valid @RequestParam String refreshToken){
        LoginResponse data = authService.refresh(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>(true, "OK", "토큰이 재발급되었습니다.", data));
    }

    @Operation(summary = "로그아웃 (RefreshToken 폐기)")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestParam String refreshToken){
        authService.logout(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>(true, "Ok", "로그아웃 되었습니다.", null));
    }

    @Operation(summary = "회원가입(local)")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest req){
        authService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "OK", "회원가입이 완료되었습니다.", null));
    }

    @Operation(summary = "id 중복확인")
    @GetMapping("/check-id")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> checkLoginId(
            @RequestParam String loginId) {

        boolean available = authQueryService.isLoginIdAvailable(loginId);
        AvailabilityResponse body = AvailabilityResponse.ofLoginId(available, loginId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", null, body)
        );
    }

    @GetMapping("/check-nickname")
    @Operation(summary = "nickname 중복확인")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> checkNickname(
            @RequestParam String nickname) {

        boolean available = authQueryService.isNicknameAvailable(nickname);
        AvailabilityResponse body = AvailabilityResponse.ofNickname(available, nickname);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "OK", null, body)
        );
    }
}
