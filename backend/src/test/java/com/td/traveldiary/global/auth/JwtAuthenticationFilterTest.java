package com.td.traveldiary.global.auth;

import com.td.traveldiary.global.auth.oauth2.CustomOAuth2UserService;
import com.td.traveldiary.global.auth.oauth2.OAuth2SuccessHandler;
import com.td.traveldiary.global.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JwtTestController.class)
@Import({SecurityConfig.class, JwtProvider.class})
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    // SecurityConfig 의존성 충족용 — 테스트에서 직접 사용하지 않음

    @MockitoBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockitoBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @Test
    void request_without_token_returns_401() throws Exception {
        mockMvc.perform(get("/test/secured"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void request_with_invalid_token_returns_401() throws Exception {
        mockMvc.perform(get("/test/secured")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticated_request_returns_200() throws Exception {
        String token = jwtProvider.generateAccessToken(1L, "ROLE_USER");

        mockMvc.perform(get("/test/secured")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}