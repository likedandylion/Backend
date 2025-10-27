package com.likedandylion.prome.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 비밀번호 변경 요청을 받을 때 사용하는 DTO (요청은 class 규칙)
 * - currentPassword: 현재 비밀번호
 * - newPassword: 새 비밀번호
 *
 * @NotBlank: 값이 비어 있으면 안 됨
 * @Size: 길이 제한(예: 8~64자) - 필요에 맞게 조정 가능
 */
public class ChangePasswordRequest {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 64, message = "현재 비밀번호는 8~64자여야 합니다.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 64, message = "새 비밀번호는 8~64자여야 합니다.")
    private String newPassword;

    // 기본 생성자 (JSON 역직렬화용)
    public ChangePasswordRequest() { }

    // 모든 필드 생성자 (테스트나 수동 생성 시 편의)
    public ChangePasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    // Getter (롬복 안 쓰는 스타일로 직접 작성)
    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
