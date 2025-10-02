package com.likedandylion.prome.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private final Key key;

    public TokenProvider(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // Access Token Generate
    public String generateAccessToken(String subject, Map<String, Object> claims){
        long now = System.currentTimeMillis();
        Date iat = new Date(now); //iat 발급시각
        Date exp = new Date(now + jwtProperties.getAccessExpirySeconds() * 1000); // exp : 만료시각

        return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())    // iss : 누가 발급
                .setSubject(subject)                     // sub : 누구에게 발급
                .addClaims(claims)                       // claims : role
                .setIssuedAt(iat)                        // iat : 발급시각
                .setExpiration(exp)                      // exp : 만료시각
                .signWith(key, SignatureAlgorithm.HS256) // 헤더 + 페이로드를 서명
                .compact();                              // 문자열 "헤더.페이로드.서명"으로 압축
    }

    // 서명/만료/형식 검증 + issuer 일치 확인
    public boolean validate(String token){
        try{
            Jws<Claims> jws = parser().parseClaimsJws(token);
            String iss = jws.getBody().getIssuer();
            return jwtProperties.getIssuer().equals(iss);
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // 서명 불일치, 만료, 손상, 빈 토큰 등
        }
    }

    // subject 추출
    public String getSubject(String token){
        return parser().parseClaimsJws(token).getBody().getSubject();
    }

    // 모둔 클레임 추출(role)
    public Claims getClaims(String token){
        return parser().parseClaimsJws(token).getBody();
    }

    // Bearer 토큰 헤더에서 순수 토큰만 분리
    public static String resolveBearer(String authorizationHeader){
        if (authorizationHeader == null) return null;
        if (!authorizationHeader.startsWith("Bearer ")) return null;
        return authorizationHeader.substring(7);
    }

    private JwtParser parser(){
        return Jwts.parserBuilder().setSigningKey(key).build();
    }
}
