package com.td.traveldiary.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/*
 * # @EnableWebSecurity
 * Spring Security 자동 설정을 끄고 직접 작성한 SecurityFilterChain이 설정을 담당하게 한다.
 *
 * # SecurityFilterChain
 * Security 필터 체인을 빈으로 등록하는 방식이다.
 * Spring Security 5.7부터 WebSecurityConfigurerAdapter 상속 대신 이 방식을 권장한다.
 *
 * # TODO: filter chain 설정하기
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth.anyRequest().permitAll()
                );

        return http.build();
    }
}