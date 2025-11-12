package com.likedandylion.prome.global.jwt;

import com.likedandylion.prome.global.security.CustomUserDetails;
import com.likedandylion.prome.global.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = TokenProvider.resolveBearer(authHeader);
        System.out.println("🔹 [JWT Filter] Authorization 헤더: " + authHeader);

        try {
            if (StringUtils.hasText(token) && tokenProvider.validate(token)) {
                String subject = tokenProvider.getSubject(token);
                System.out.println("✅ [JWT Filter] 토큰 유효함, subject=" + subject);

                CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(subject);
                System.out.println("✅ [JWT Filter] 사용자 인증 성공: " + user.getUsername());

                if (SecurityContextHolder.getContext().getAuthentication() == null){
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                System.out.println("❌ [JWT Filter] 토큰 없음 또는 유효하지 않음");
            }
        } catch (Exception e){
            System.out.println("❌ [JWT Filter] 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")
                || path.startsWith("/favicon")
                || path.equals("/")
                || path.contains("swagger");
    }
}