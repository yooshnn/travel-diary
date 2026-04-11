package com.td.traveldiary.global.auth.oauth2;

import java.util.Map;

/*
 * Google OAuth2 사용자 정보 추출 구현체.
 * Google은 사용자 고유 ID를 'sub' 클레임으로 제공한다.
 */
public class GoogleOAuth2MemberInfo implements OAuth2MemberInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2MemberInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
