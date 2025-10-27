package com.likedandylion.prome.user.dto;

/**
 * 응답 DTO는 record (불변/안정)
 * - 요청한 형태(status, message)를 그대로 담아 반환
 */
public record UpdateProfileResponse(
        String status,
        String message
) {
    /** 성공 응답을 간단히 만들기 위한 헬퍼 */
    public static UpdateProfileResponse success() {
        return new UpdateProfileResponse("success", "프로필이 성공적으로 변경되었습니다.");
    }
}
