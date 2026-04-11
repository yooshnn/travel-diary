package com.td.traveldiary.global.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
 * OncePerRequestFilter: 요청당 한 번만 실행됨을 보장하는 필터 추상 클래스.
 *
 * 서블릿 스펙에서 forward/include 같은 내부 디스패치가 발생하면 필터 체인이 다시 실행될 수 있다.
 * 예를 들어 컨트롤러에서 다른 뷰로 forward하거나, 에러 페이지로 디스패치될 때 필터가 두 번 탈 수 있다.
 * OncePerRequestFilter는 요청 속성(attribute)에 실행 여부를 기록해 이미 실행된 요청은 건너뛴다.
 * JWT 검증처럼 요청당 정확히 한 번만 실행되어야 하는 로직에 적합하다.
 *
 * doFilterInternal: OncePerRequestFilter가 중복 실행을 막은 뒤 위임하는 실제 필터 로직.
 * 일반 Filter라면 doFilter()를 오버라이드하지만, OncePerRequestFilter는 doFilterInternal()에
 * 비즈니스 로직을 작성한다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        // 토큰이 존재하고, 유효한 경우 SecurityContext에 인증 정보를 저장한다.
        if (token != null) {
            try {
                jwtProvider.validateToken(token);

                // 토큰이 유효하면 토큰에서 유저 정보 획득
                Long memberId = jwtProvider.getMemberId(token);
                String role = jwtProvider.getRole(token);

                // 인증 정보 생성
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
                Authentication authentication = new UsernamePasswordAuthenticationToken(memberId, "", authorities);

                // SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.debug("Invalid JWT token for request: {}", request.getRequestURI());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
