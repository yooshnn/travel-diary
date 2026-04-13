package com.td.traveldiary.domain.member.service;

import com.td.traveldiary.domain.member.entity.Member;
import com.td.traveldiary.domain.member.entity.Provider;
import com.td.traveldiary.domain.member.entity.Role;
import com.td.traveldiary.domain.member.exception.MemberNotFoundException;
import com.td.traveldiary.domain.member.repository.MemberRepository;
import com.td.traveldiary.domain.member.service.dto.response.MemberInfo;
import com.td.traveldiary.global.file.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FileStorageService fileStorageService;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(1L)
                .name("홍길동")
                .provider(Provider.GOOGLE)
                .providerId("google-id")
                .role(Role.USER)
                .profileImageUrl(null)
                .isDeleted(false)
                .build();
    }

    @Test
    void getMyProfile_returns_member_info() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberInfo result = memberService.getMyProfile(1L);

        assertThat(result.name()).isEqualTo("홍길동");
        assertThat(result.role()).isEqualTo("USER");
    }

    @Test
    void getMyProfile_throws_when_member_not_found() {
        when(memberRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMyProfile(-1L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void updateMyProfile_updates_name() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberInfo result = memberService.updateMyProfile(1L, "새이름", null);

        assertThat(result.name()).isEqualTo("새이름");
        verify(memberRepository).update(member);
        verify(fileStorageService, never()).store(any());
    }

    @Test
    void updateMyProfile_updates_profile_image() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(fileStorageService.store(any())).thenReturn("http://localhost:8080/uploads/new.jpg");

        MockMultipartFile image = new MockMultipartFile(
                "profileImage", "new.jpg", "image/jpeg", "content".getBytes());

        MemberInfo result = memberService.updateMyProfile(1L, "홍길동", image);

        assertThat(result.profileImageUrl()).isEqualTo("http://localhost:8080/uploads/new.jpg");
        verify(fileStorageService).store(any());
        verify(fileStorageService, never()).delete(any()); // 기존 이미지 없으니까 delete 호출 안 함
    }

    @Test
    void updateMyProfile_deletes_old_image_when_updating() {
        Member memberWithImage = Member.builder()
                .id(1L)
                .name("홍길동")
                .provider(Provider.GOOGLE)
                .providerId("google-id")
                .role(Role.USER)
                .profileImageUrl("http://localhost:8080/uploads/old.jpg")
                .isDeleted(false)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(memberWithImage));
        when(fileStorageService.store(any())).thenReturn("http://localhost:8080/uploads/new.jpg");

        MockMultipartFile image = new MockMultipartFile(
                "profileImage", "new.jpg", "image/jpeg", "content".getBytes());

        memberService.updateMyProfile(1L, "홍길동", image);

        verify(fileStorageService).delete("http://localhost:8080/uploads/old.jpg");
        verify(fileStorageService).store(any());
    }
}