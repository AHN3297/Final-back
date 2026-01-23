package com.kh.replay.global.config;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.kh.replay.auth.oauth.model.vo.CustomOAuth2User;
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

        // JWT 생성 (memberId, email, role)
        String token = jwtUtil.getAccessToken(memberId, email, role,name);

        
        // 리다이렉트 경로 설정
        String redirectTo = customUser.isNewUser() 
            ? "api/oauth/social/addsocialInfo"     // 신규 회원 → 추가 정보 입력 페이지
            : "http://localhost:5173/main";        // 기존 회원 → 메인 페이지

        
        String redirectUrl = "http://localhost:5173/oauth-callback?token=" + token + "&redirectTo=" + redirectTo;
        getRedirectStrategy().sendRedirect(
        		request, response, redirectUrl
        );
    }
}