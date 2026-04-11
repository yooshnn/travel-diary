package com.td.traveldiary.global.config;

import com.td.traveldiary.global.auth.JwtAuthenticationFilter;
import com.td.traveldiary.global.auth.JwtProvider;
import com.td.traveldiary.global.auth.oauth2.CustomOAuth2UserService;
import com.td.traveldiary.global.auth.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/*
 * # @EnableWebSecurity
 * Spring Security 자동 설정을 끄고 직접 작성한 SecurityFilterChain이 설정을 담당하게 한다.
 *
 * # SecurityFilterChain
 * Security 필터 체인을 빈으로 등록하는 방식이다.
 * Spring Security 5.7부터 WebSecurityConfigurerAdapter 상속 대신 이 방식을 권장한다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtProvider jwtProvider;

    @Value("${app.cors.allowed-origins}")
    private List<String> allowedOrigins;
    @Value("${security.whitelist}")
    private String[] whiteList;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 비활성화 (세션을 사용하지 않고, JWT를 Authorization 헤더로 전송하고 있어 CSRF 공격으로부터 안전한 편)
                .csrf(AbstractHttpConfigurer::disable)

                // Form 로그인 & HttpBasic 비활성화 (OAuth + JWT 사용)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션 사용 안 함
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인증 없이 보호된 엔드포인트 접근 시 401 반환
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // URL별 권한 관리
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // 유저 정보 가져오는 엔드포인트 설정
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        // 로그인 성공 시 핸들러 설정 (여기서 토큰 발급)
                        .successHandler(oAuth2SuccessHandler)
                )

                // JWT 필터를 ID/PW 인증 필터보다 먼저 실행되도록 등록
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        if (allowedOrigins.contains("*")) {
            throw new IllegalArgumentException("CORS allowed-origins에 '*'는 사용할 수 없습니다.");
        }

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}