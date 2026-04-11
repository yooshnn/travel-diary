package com.td.traveldiary.global.auth.oauth2;

import com.td.traveldiary.domain.member.entity.Member;
import com.td.traveldiary.domain.member.entity.Provider;
import com.td.traveldiary.domain.member.entity.Role;
import com.td.traveldiary.domain.member.repository.MemberRepository;
import com.td.traveldiary.global.exception.BusinessException;
import com.td.traveldiary.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/*
 * OAuth2 로그인 성공 후 사용자 정보를 처리하는 서비스.
 * DB에 가입된 회원이면 기존 정보를 반환하고,
 * 신규 회원이면 INSERT 후 반환한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2MemberInfo memberInfo = switch (registrationId) {
            case "google" -> new GoogleOAuth2MemberInfo(oAuth2User.getAttributes());
            default -> throw new BusinessException(ErrorCode.UNSUPPORTED_PROVIDER);
        };

        Member member = upsert(memberInfo);

        return new CustomOAuth2User(
                member.getId(),
                member.getRole().name(),
                oAuth2User.getAttributes(),
                nameAttributeKey
        );
    }

    private Member upsert(OAuth2MemberInfo memberInfo) {
        return memberRepository.findByProviderId(memberInfo.getProviderId())
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .name(memberInfo.getName())
                            .provider(Provider.GOOGLE)
                            .providerId(memberInfo.getProviderId())
                            .role(Role.USER)
                            .profileImageUrl(null)
                            .isDeleted(false)
                            .build();
                    memberRepository.save(newMember);
                    return newMember;
                });
    }
}
