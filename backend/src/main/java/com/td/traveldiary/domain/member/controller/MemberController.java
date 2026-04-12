package com.td.traveldiary.domain.member.controller;

import com.td.traveldiary.domain.member.controller.dto.request.MemberUpdateRequest;
import com.td.traveldiary.domain.member.controller.dto.response.MemberInfoResponse;
import com.td.traveldiary.domain.member.service.MemberService;
import com.td.traveldiary.domain.member.service.dto.response.MemberInfo;
import com.td.traveldiary.global.annotation.CurrentMemberId;
import com.td.traveldiary.global.annotation.ValidImageFile;
import com.td.traveldiary.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ApiResponse<MemberInfoResponse> getMyProfile(@CurrentMemberId Long memberId) {
        MemberInfo memberInfo = memberService.getMyProfile(memberId);
        MemberInfoResponse response = MemberInfoResponse.from(memberInfo);

        return ApiResponse.onSuccess(response);
    }

    @PatchMapping("/me")
    public ApiResponse<MemberInfoResponse> updateMyProfile(
            @CurrentMemberId Long memberId,
            @Valid @ModelAttribute MemberUpdateRequest request) {

        MultipartFile validatedImage = (request.profileImage() != null && !request.profileImage().isEmpty())
                ? request.profileImage() : null;

        MemberInfo memberInfo = memberService.updateMyProfile(memberId, request.name(), validatedImage);
        MemberInfoResponse response = MemberInfoResponse.from(memberInfo);

        return ApiResponse.onSuccess(response);
    }
}
