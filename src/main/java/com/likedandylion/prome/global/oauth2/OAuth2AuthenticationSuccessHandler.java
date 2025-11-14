package com.likedandylion.prome.global.oauth2;

import com.likedandylion.prome.auth.util.RefreshTokenUtil;
import com.likedandylion.prome.global.jwt.TokenProvider;
import com.likedandylion.prome.global.security.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenUtil refreshTokenUtil;

    private static final String FRONTEND_REDIRECT_URL = "https://promefe.vercel.app/login";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String loginId = userDetails.getUsername();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        log.info("OAuth2 로그인 성공. JWT 토큰 발급을 시작합니다. User: {}", loginId);

        Map<String, Object> claims = Map.of("role", role);
        String accessToken = tokenProvider.generateAccessToken(loginId, claims);
        String refreshToken = refreshTokenUtil.createAndSaveRefreshToken(loginId);

        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_REDIRECT_URL)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}