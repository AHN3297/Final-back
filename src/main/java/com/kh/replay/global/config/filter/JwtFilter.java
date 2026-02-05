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

        // OAuth2 흐름 (Spring Security OAuth2가 처리)
        if (uri.startsWith("/oauth2/")) return true;
        if (uri.startsWith("/login/oauth2/")) return true;
        if (uri.startsWith("/login/")) return true;

        // 정적 리소스
        if (uri.equals("/favicon.ico")) return true;
        if (uri.startsWith("/.well-known/")) return true;
        if (uri.equals("/error")) return true;
        if (uri.startsWith("/css/")) return true;
        if (uri.startsWith("/js/")) return true;
        if (uri.startsWith("/images/")) return true;

        // API는 모두 필터를 거침 (SecurityFilterChain에서 permitAll 처리)
        return false;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰이 없으면 그대로 통과
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            Claims claims = jwtUtil.parseJwt(token);

            String memberId = claims.getSubject();
            String role = claims.get("role", String.class);

            // role 기본값 및 정제
            if (!StringUtils.hasText(role)) {
                role = "ROLE_USER";
            }
            role = role.replace("[", "").replace("]", "").trim();

            // memberId로 사용자 정보 조회
            CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByMemberId(memberId);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    user, 
                    null, 
                    user.getAuthorities()
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            // DB 조회 실패 등
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }}