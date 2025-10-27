package com.likedandylion.prome.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 클라이언트가 보낸 프로필 수정 값(닉네임, 이미지URL)을 담는 요청 DTO
 * - Request DTO는 class (유연하게 확장하기 쉬움)
 */
@Getter @Setter
@NoArgsConstructor
public class UpdateProfileRequest {

    @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
    @Size(max = 30, message = "닉네임은 최대 30자까지 가능합니다.")
    private String nickname;            // 예: "위대한 케로로 대장"

    @Size(max = 500, message = "프로필 이미지 URL은 최대 500자까지 가능합니다.")
    private String profileImageUrl;     // 예: "new_keroro_image_url"
}
