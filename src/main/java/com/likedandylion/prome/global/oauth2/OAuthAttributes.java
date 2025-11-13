package com.likedandylion.prome.global.oauth2;

import com.likedandylion.prome.user.entity.Provider;
import com.likedandylion.prome.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@Slf4j
public class OAuthAttributes {

    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String loginId;
    private final String nickname;
    private final String profileImageUrl;
    private final Provider provider;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey,
                           String loginId, String nickname, String profileImageUrl, Provider provider) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.loginId = loginId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        log.warn("Unsupported OAuth2 provider: {}", registrationId);
        throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다.");
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        String kakaoId = String.valueOf(attributes.get(userNameAttributeName));
        String loginId = "kakao_" + kakaoId;
        String nickname = (String) kakaoProfile.get("nickname");
        String profileImageUrl = (String) kakaoProfile.get("profile_image_url");

        return OAuthAttributes.builder()
                .loginId(loginId)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .provider(Provider.KAKAO)
                .nameAttributeKey(userNameAttributeName)
                .attributes(attributes)
                .build();
    }

    public User toEntity() {
        return User.fromOAuth(
                loginId,
                nickname,
                profileImageUrl,
                provider
        );
    }
}