package com.kh.replay.global.config.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kh.replay.auth.token.util.JwtUtil;
import com.kh.replay.member.model.service.UserDetailsServiceImpl;
import com.kh.replay.member.model.vo.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

  
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // OAuth2 흐름(여기는 절대 JWT로 건드리면 안 됨)
        if (uri.startsWith("/oauth2/")) return true;
        if (uri.startsWith("/login/oauth2/")) return true;
        if (uri.startsWith("/login/")) return true;
        if (uri.equals("/oauth-callback")) return true;

        // 공개 엔드포인트(당신 프로젝트 기준으로 정리)
        if (uri.equals("/api/members/login")) return true;
        if (uri.equals("/api/auth/login")) return true;     // 실제로 쓰는지 확인 필요
        if (uri.startsWith("/api/auth/signUp")) return true;
        if (uri.startsWith("/api/members/signup")) return true; // 대소문자/경로 통일 권장
        if (uri.startsWith("/test/")) return true;

        return uri.equals("/favicon.ico")
        || uri.startsWith("/.well-known/")
        || uri.equals("/error")
        || uri.startsWith("/css/")
        || uri.startsWith("/js/")
        || uri.startsWith("/images/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // ✅ 토큰이 없으면: 막지 말고 그대로 통과
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            Claims claims = jwtUtil.parseJwt(token);

            String memberId = claims.getSubject();
            String role = claims.get("role", String.class);

            // role 기본값 + 정제
            if (!StringUtils.hasText(role)) role = "ROLE_USER";
            role = role.replace("[", "").replace("]", "").trim();
            boolean isSocialLike = (memberId != null) && (memberId.startsWith("#") || memberId.matches("^[0-9]+$"));

            UsernamePasswordAuthenticationToken authentication;

            if (isSocialLike) {
                CustomUserDetails socialUser = CustomUserDetails.builder()
                        .username(memberId)
                        .memberName("Social User")
                        .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                        .build();

                authentication = new UsernamePasswordAuthenticationToken(
                        socialUser, null, socialUser.getAuthorities()
                );

            } else {
                CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(memberId);

                authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                );
            }

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        	}

        filterChain.doFilter(request, response);
    }
}
