package com.td.traveldiary.domain.member.controller.dto.request;

import com.td.traveldiary.global.file.ValidImageFile;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record MemberUpdateRequest(
        @NotBlank String name,
        @ValidImageFile MultipartFile profileImage
) {}
