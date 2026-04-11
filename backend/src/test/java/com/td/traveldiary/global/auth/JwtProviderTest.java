package com.td.traveldiary.global.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        String secret = "KvJJfzso44f3ryXS2X4zGUmcGjwt9x8udDOW36HsKao=";
        long expiration = 3600000L; // 1시간
        jwtProvider = new JwtProvider(secret, expiration);
    }

    @Test
    void generateAccessToken_returns_valid_token() {
        String token = jwtProvider.generateAccessToken(1L, "ROLE_USER");
        assertThat(token).isNotBlank();
    }

    @Test
    void getMemberId_returns_correct_memberId() {
        String token = jwtProvider.generateAccessToken(1L, "ROLE_USER");
        assertThat(jwtProvider.getMemberId(token)).isEqualTo(1L);
    }

    @Test
    void getRole_returns_correct_role() {
        String token = jwtProvider.generateAccessToken(1L, "ROLE_USER");
        assertThat(jwtProvider.getRole(token)).isEqualTo("ROLE_USER");
    }

    @Test
    void validateToken_throws_exception_when_token_expired() {
        JwtProvider expiredJwtProvider = new JwtProvider(
                "KvJJfzso44f3ryXS2X4zGUmcGjwt9x8udDOW36HsKao=",
                -1000L // 만료
        );
        String token = expiredJwtProvider.generateAccessToken(1L, "ROLE_USER");

        assertThatThrownBy(() -> jwtProvider.validateToken(token))
                .isInstanceOf(Exception.class);
    }

    @Test
    void validateToken_throws_exception_when_token_tampered() {
        String token = jwtProvider.generateAccessToken(1L, "ROLE_USER");
        String tamperedToken = token + "hello";

        assertThatThrownBy(() -> jwtProvider.validateToken(tamperedToken))
                .isInstanceOf(Exception.class);
    }
}