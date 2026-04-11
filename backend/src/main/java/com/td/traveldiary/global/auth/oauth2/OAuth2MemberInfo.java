package com.td.traveldiary.global.auth.oauth2;

/*
 * OAuth2 제공자(Google, Kakao 등)마다 사용자 정보 구조가 다르다.
 * 이 인터페이스로 추상화해두면 CustomOAuth2UserService는 제공자에 관계없이
 * 동일한 방식으로 사용자 정보를 다룰 수 있다.
 */
public interface OAuth2MemberInfo {
    String getProviderId();
    String getName();
}
