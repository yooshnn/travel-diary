package com.td.traveldiary.global.auth.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
 * Spring Security의 OAuth2User 구현체.
 * 기본 DefaultOAuth2User는 memberId를 직접 꺼내기 어렵다.
 * memberId와 role을 필드로 들고 있어 OAuth2SuccessHandler에서
 * JWT 발급 시 바로 꺼내 쓸 수 있다.
 */
public class CustomOAuth2User implements OAuth2User {

    private final Long memberId;
    private final String role;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    public CustomOAuth2User(Long memberId, String role, Map<String, Object> attributes, String nameAttributeKey) {
        this.memberId = memberId;
        this.role = role;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get(nameAttributeKey));
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getRole() {
        return role;
    }
}
