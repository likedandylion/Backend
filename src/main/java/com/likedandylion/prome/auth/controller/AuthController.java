package com.likedandylion.prome.auth.controller;

import com.likedandylion.prome.auth.dto.LoginRequest;
import com.likedandylion.prome.auth.dto.LoginResponse;
import com.likedandylion.prome.auth.service.AuthService;
import com.likedandylion.prome.global.wrapper.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}
