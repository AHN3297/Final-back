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

    @Value("${front.base-url:http://localhost:5173}")
    private String frontBaseUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();

        String memberId = customUser.getMemberId();
        String email = customUser.getEmail();
        String name = customUser.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty()
                ? "ROLE_USER"
                : authorities.iterator().next().getAuthority();

      
        String accessToken = jwtUtil.getAccessToken(memberId, email, role, name);
        String refreshTokenValue = jwtUtil.getRefreshToken(memberId);

        try {
            long expirationMillis = System.currentTimeMillis()
                    + (1000L * 60 * 60 * 24 * 14);

            RefreshToken refreshToken = RefreshToken.builder()
                    .memberId(memberId)
                    .token(refreshTokenValue)
                    .expiration(new Date(expirationMillis)) 
                    .createdAt(new Date())
                    .build();

            tokenMapper.insertToken(refreshToken);

        } catch (Exception e) {
            log.error("리프레시 토큰 저장 실패 memberId={}", memberId, e);
        }

        String targetPath = customUser.isNewUser()
                ? "/oauth/step2"
                : "/main";

        String at = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
        String rt = URLEncoder.encode(refreshTokenValue, StandardCharsets.UTF_8);
        String nm = URLEncoder.encode(name == null ? "" : name, StandardCharsets.UTF_8);
        String em = URLEncoder.encode(email == null ? "" : email, StandardCharsets.UTF_8);

        String redirectUrl = frontBaseUrl + targetPath
                + "?accessToken=" + at
                + "&refreshToken=" + rt
                + "&name=" + nm
                + "&email=" + em;

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
