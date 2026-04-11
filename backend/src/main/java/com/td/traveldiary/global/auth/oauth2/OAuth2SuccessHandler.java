package com.td.traveldiary.global.auth.oauth2;

import com.td.traveldiary.global.auth.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.oauth2.callback-path}")
    private String callbackPath;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long memberId = oAuth2User.getMemberId();
        String role = oAuth2User.getRole();

        String token = jwtProvider.generateAccessToken(memberId, role);
        String url = frontendUrl + callbackPath + "?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, url);
    }
}
