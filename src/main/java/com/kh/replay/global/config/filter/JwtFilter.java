package com.kh.replay.global.config.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    /**
     * 🔓 JWT 필터 완전 제외 경로
     */
    private static final List<String> WHITELIST = List.of(
        "/api/members/login",
        "/api/members/signup",
        "/api/members/complete",
        "/api/auth/login",
        "/api/auth/signUp",
        "/oauth-callback"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // OPTIONS 무조건 통과
        if (HttpMethod.OPTIONS.matches(method)) {
            return true;
        }
        // 화이트리스트
        for (String path : WHITELIST) {
            if (uri.startsWith(path)) {
                return true;
            }
        }

        // 인증 이전 API
        if (uri.equals("/api/members/login")) return true;
        if (uri.equals("/api/auth/login")) return true;
        if (uri.equals("/api/auth/signUp")) return true;
        if (uri.equals("/api/members/genres")) return true;
        if (uri.equals("/members/login")) return true;
        if (uri.equals("/login")) return true;
        if (uri.equals("/api/members/signup") || uri.startsWith("/api/members/signup")) return true;


        // OAuth2
        if (uri.startsWith("/oauth2/") || uri.startsWith("/login/oauth2/")) {
            return true;
        }

        // WebSocket
        if (uri.startsWith("/ws-chat")) {
            return true;
        }

        // 정적 리소스
        if (uri.equals("/favicon.ico") ||
            uri.equals("/error") ||
            uri.startsWith("/css/") ||
            uri.startsWith("/js/") ||
            uri.startsWith("/images/")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 🔐 토큰 없으면 그냥 통과 (Security 쪽에서 판단)
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            Claims claims = jwtUtil.parseJwt(token);

            String memberId = claims.getSubject();
            if (!StringUtils.hasText(memberId)) {
                throw new JwtException("JWT subject(memberId) 없음");
            }

            CustomUserDetails user =
                (CustomUserDetails) userDetailsService.loadUserByMemberId(memberId);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    user,
                    null,
                    user.getAuthorities()
                );

            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
