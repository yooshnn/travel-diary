package com.td.traveldiary.domain.member.controller.dto.response;

import com.td.traveldiary.domain.member.service.dto.response.MemberInfo;

public record MemberInfoResponse(
        long id,
        String name,
        String role,
        String profileImageUrl
) {
    public static MemberInfoResponse from(MemberInfo memberInfo) {
        return new MemberInfoResponse(
                memberInfo.id(),
                memberInfo.name(),
                memberInfo.role(),
                memberInfo.profileImageUrl()
        );
    }
}
