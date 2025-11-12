package com.likedandylion.prome.user.dto;

/**
 * 비밀번호 변경 성공 시 내려줄 응답 DTO (응답은 record 규칙)
 * - status: "success"
 * - message: "비밀번호가 성공적으로 변경되었습니다."
 *
 * 컨트롤러에서는 ApiResponse<ChangePasswordResponse>로 감싸서 반환합니다.
 */
public record ChangePasswordResponse(
        String status,
        String message
) {
    /**
     * 흔히 쓸 성공 응답을 쉽게 만들기 위한 팩토리 메서드
     */
    public static ChangePasswordResponse success() {
        return new ChangePasswordResponse("success", "비밀번호가 성공적으로 변경되었습니다.");
    }
}