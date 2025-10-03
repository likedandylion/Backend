package com.likedandylion.prome.auth.dto;

public record LoginResponse(
        String accessToken, String refreshToken
) {
}
