package com.td.traveldiary.domain.member.controller.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record MemberUpdateRequest(
        String name,
        MultipartFile profileImage
) {}
