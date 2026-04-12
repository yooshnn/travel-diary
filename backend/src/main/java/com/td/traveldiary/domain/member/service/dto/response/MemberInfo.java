package com.td.traveldiary.domain.member.service.dto.response;

import com.td.traveldiary.domain.member.entity.Member;

public record MemberInfo(
        long id,
        String name,
        String role,
        String profileImageUrl
) {
    public static MemberInfo from(Member member) {
        return new MemberInfo(
                member.getId(),
                member.getName(),
                member.getRole().name(),
                member.getProfileImageUrl()
        );
    }
}
