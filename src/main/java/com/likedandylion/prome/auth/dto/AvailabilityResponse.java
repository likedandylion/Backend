package com.likedandylion.prome.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AvailabilityResponse {
    private boolean available;
    private String message;

    public static AvailabilityResponse ofLoginId(boolean available, String loginId) {
        return available
                ? new AvailabilityResponse(true, "사용 가능한 아이디입니다.")
                : new AvailabilityResponse(false, "이미 사용 중인 아이디입니다.");
    }

    public static AvailabilityResponse ofNickname(boolean available, String nickname) {
        return available
                ? new AvailabilityResponse(true, "사용 가능한 닉네임입니다.")
                : new AvailabilityResponse(false, "이미 사용 중인 닉네임입니다.");
    }
}