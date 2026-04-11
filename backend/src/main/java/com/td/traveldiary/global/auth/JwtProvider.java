package com.td.traveldiary.global.auth;

import com.td.traveldiary.global.exception.BusinessException;
import com.td.traveldiary.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration}") long accessTokenValidityInMilliseconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
    }

    public String generateAccessToken(Long memberId, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .subject(String.valueOf(memberId))  // 사용자 식별값(이메일이나 ID 등)
                .claim("role", role)             // 사용자 권한 정보
                .issuedAt(now)                      // 발급 시각
                .expiration(validity)               // 만료 시각
                .signWith(key)                      // 암호화 알고리즘과 비밀키로 서명
                .compact();
    }

    public Claims getPayload(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getMemberId(String token) {
        Claims payload = getPayload(token);
        String subject = payload.getSubject();

        try {
            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            throw new JwtException("Token payload is not a valid User ID");
        }
    }

    public String getRole(String token) {
        Claims payload = getPayload(token);
        return payload.get("role", String.class);
    }

    public void validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }
}
