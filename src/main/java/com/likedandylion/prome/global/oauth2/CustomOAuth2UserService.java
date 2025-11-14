package com.likedandylion.prome.global.oauth2;

import com.likedandylion.prome.global.security.CustomUserDetails;
import com.likedandylion.prome.user.entity.User;
import com.likedandylion.prome.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("OAuth2 유저 정보 로드를 시작합니다.");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        Map<String, Object> attributesMap = oAuth2User.getAttributes();

        if (registrationId.equals("kakao") && attributesMap.containsKey(userNameAttributeName) && !attributesMap.containsKey("name")) {
            Map<String, Object> mutableAttributes = new HashMap<>(attributesMap);
            String kakaoId = attributesMap.get(userNameAttributeName).toString();
            mutableAttributes.put("name", kakaoId);
            attributesMap = mutableAttributes;

            oAuth2User = new DefaultOAuth2User(oAuth2User.getAuthorities(), attributesMap, "name");
        }

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributesMap);

        User user = saveOrUpdate(attributes);

        log.info("OAuth2 유저({}:{}) 로그인 성공. CustomUserDetails 객체를 반환합니다.", user.getProvider(), user.getLoginId());

        return new CustomUserDetails(user, attributesMap);
    }

    @Transactional
    public User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByLoginId(attributes.getLoginId())
                .map(entity -> entity.updateOAuthProfile(attributes.getNickname(), attributes.getProfileImageUrl()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}