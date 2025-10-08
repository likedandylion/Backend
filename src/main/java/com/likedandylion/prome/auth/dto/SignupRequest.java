package com.likedandylion.prome.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 4, max = 20, message = "학번을 입력해주세요.")
    private String loginId;

    @NotBlank @Size(min = 2, max = 20, message = "이름을 입력해주세요.")
    private String nickname;

    @NotBlank @Size(min = 8, max = 20, message = "비밀번호는 8~20자여야 합니다.")
    private String password;

    @NotBlank 
    private String passwordConfirm;
}
