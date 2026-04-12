package com.td.traveldiary.global.resolver;

import com.td.traveldiary.global.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class CurrentMemberIdArgumentResolverTest {

    private final CurrentMemberIdArgumentResolver resolver = new CurrentMemberIdArgumentResolver();

    @Test
    void resolveArgument_returns_memberId_when_authenticated() throws Exception {
        // JwtAuthenticationFilter에서 principal에 `Long memberId`를 저장하기로 했다.
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, "", List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Long memberId = resolver.resolveArgument(null, null, null, null);

        assertThat(memberId).isEqualTo(1L);
    }

    @Test
    void resolveArgument_throws_when_not_authenticated() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> resolver.resolveArgument(null, null, null, null))
                .isInstanceOf(BusinessException.class);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}