package com.td.traveldiary.domain.member.service;

import com.td.traveldiary.domain.member.entity.Member;
import com.td.traveldiary.domain.member.exception.MemberNotFoundException;
import com.td.traveldiary.domain.member.repository.MemberRepository;
import com.td.traveldiary.domain.member.service.dto.response.MemberInfo;
import com.td.traveldiary.global.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;

    public MemberInfo getMyProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        return MemberInfo.from(member);
    }

    public MemberInfo updateMyProfile(Long memberId, String name, MultipartFile profileImage) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.updateName(name);
        updateProfileImage(member, profileImage);
        memberRepository.update(member);

        return MemberInfo.from(member);
    }

    private void updateProfileImage(Member member, MultipartFile profileImage) {
        if (profileImage == null) return;

        if (member.getProfileImageUrl() != null) {
            fileStorageService.delete(member.getProfileImageUrl());
        }
        member.updateProfileImageUrl(fileStorageService.store(profileImage));
    }
}
