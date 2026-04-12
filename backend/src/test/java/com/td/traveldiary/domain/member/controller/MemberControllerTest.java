package com.td.traveldiary.domain.member.controller;

import com.td.traveldiary.domain.member.service.MemberService;
import com.td.traveldiary.domain.member.service.dto.response.MemberInfo;
import com.td.traveldiary.global.auth.JwtProvider;
import com.td.traveldiary.global.auth.oauth2.CustomOAuth2UserService;
import com.td.traveldiary.global.auth.oauth2.OAuth2SuccessHandler;
import com.td.traveldiary.global.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@Import({SecurityConfig.class, JwtProvider.class})
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    @MockitoBean
    private MemberService memberService;

    // SecurityConfig 의존성 충족용 — 테스트에서 직접 사용하지 않음
    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    private String token;

    @BeforeEach
    void setUp() {
        token = jwtProvider.generateAccessToken(1L, "ROLE_USER");
    }

    @Test
    void getMyProfile_returns_200() throws Exception {
        MemberInfo memberInfo = new MemberInfo(1L, "홍길동", "USER", null);
        when(memberService.getMyProfile(1L)).thenReturn(memberInfo);

        mockMvc.perform(get("/api/v1/member/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("홍길동"));
    }

    @Test
    void getMyProfile_returns_401_without_token() throws Exception {
        mockMvc.perform(get("/api/v1/member/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateMyProfile_returns_200() throws Exception {
        MemberInfo memberInfo = new MemberInfo(1L, "새이름", "USER", null);
        when(memberService.updateMyProfile(eq(1L), eq("새이름"), any())).thenReturn(memberInfo);

        mockMvc.perform(multipart("/api/v1/member/me")
                        .param("name", "새이름")
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("새이름"));
    }

    @Test
    void updateMyProfile_returns_400_with_invalid_image() throws Exception {
        MockMultipartFile invalidFile = new MockMultipartFile(
                "profileImage", "doc.pdf", "application/pdf", "content".getBytes());

        mockMvc.perform(multipart("/api/v1/member/me")
                        .file(invalidFile)
                        .param("name", "새이름")
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .header("Authorization", "Bearer " + token))
                .andDo(print())  // 추가
                .andExpect(status().isBadRequest());
    }
}