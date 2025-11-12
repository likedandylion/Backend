package com.likedandylion.prome.global.jwt;

import io.jsonwebtoken.*; // Jwts, Claims, Jws, JwtException, JwtParser
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
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
        Date iat = new Date(now); // iat 발급시각
        // [수정] long 타입 계산을 위해 1000L 사용
        Date exp = new Date(now + jwtProperties.getAccessExpirySeconds() * 1000L); // exp : 만료시각

        // [수정] Deprecated(사용 중단)된 메서드들을 최신 빌더 패턴으로 변경
        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())    // setIssuer() -> issuer()
                .subject(subject)                     // setSubject() -> subject()
                .claims(claims)                       // addClaims() -> claims()
                .issuedAt(iat)                        // setIssuedAt() -> issuedAt()
                .expiration(exp)                      // setExpiration() -> expiration()
                .signWith(key)                        // signWith(key, SignatureAlgorithm) -> signWith(key)
                .compact();                           // "헤더.페이로드.서명"으로 압축
    }

    // 서명/만료/형식 검증 + issuer 일치 확인
    public boolean validate(String token){
        try{
            // [수D 1/2] Jws<Claims> jws = parser().parseClaimsJws(token);
            // 최신 버전에서는 getPayload()를 통해 Claims를 가져옵니다.
            Jws<Claims> jws = parser().parseSignedClaims(token); // parseClaimsJws() -> parseSignedClaims()
            String iss = jws.getPayload().getIssuer(); // getBody() -> getPayload()
            return jwtProperties.getIssuer().equals(iss);
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // 서명 불일치, 만료, 손상, 빈 토큰 등
        }
    }

    // subject 추출
    public String getSubject(String token){
        // [수정 2/2] getBody() -> getPayload()
        return parser().parseSignedClaims(token).getPayload().getSubject();
    }

    // 모둔 클레임 추출(role)
    public Claims getClaims(String token){
        // [수정] getBody() -> getPayload()
        return parser().parseSignedClaims(token).getPayload();
    }

    // Bearer 토큰 헤더에서 순수 토큰만 분리
    public static String resolveBearer(String authorizationHeader){
        if (authorizationHeader == null) return null;
        if (!authorizationHeader.startsWith("Bearer ")) return null;
        return authorizationHeader.substring(7);
    }

    private JwtParser parser(){
        return Jwts.parser().verifyWith((SecretKey) key).build();
    }
}