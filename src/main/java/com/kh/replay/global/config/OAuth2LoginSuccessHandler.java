package com.kh.replay.global.config;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.kh.replay.auth.oauth.model.vo.CustomOAuth2User;
import com.kh.replay.auth.token.model.dao.TokenMapper;
import com.kh.replay.auth.token.model.vo.RefreshToken;
import com.kh.replay.auth.token.util.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final TokenMapper tokenMapper;

    @Value("${front.base-url}")
    private String frontBaseUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUser =
                (CustomOAuth2User) authentication.getPrincipal();

        String memberId = customUser.getMemberId();
        String email = customUser.getEmail();
        String name = customUser.getName();

        boolean profileCompleted = customUser.isProfileCompleted();
        boolean needAdditionalInfo = !profileCompleted;

        Collection<? extends GrantedAuthority> authorities =
                authentication.getAuthorities();

        String role = authorities.isEmpty()
                ? "ROLE_USER"
                : authorities.iterator().next().getAuthority();

        String accessToken =
                jwtUtil.getAccessToken(memberId, email, role, name);

        String refreshTokenValue =
                jwtUtil.getRefreshToken(memberId);

        RefreshToken refreshToken = RefreshToken.builder()
                .memberId(memberId)
                .token(refreshTokenValue)
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14))
                .createdAt(new Date())
                .build();

        tokenMapper.insertToken(refreshToken);

        String targetPath = "/oauth/callback";

        String redirectUrl = frontBaseUrl + targetPath
                + "?accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                + "&refreshToken=" + URLEncoder.encode(refreshTokenValue, StandardCharsets.UTF_8)
                + "&memberId=" + URLEncoder.encode(memberId, StandardCharsets.UTF_8)
                + "&name=" + URLEncoder.encode(name == null ? "" : name, StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(email == null ? "" : email, StandardCharsets.UTF_8)
                + "&needAdditionalInfo=" + needAdditionalInfo;



        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
