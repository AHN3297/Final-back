package com.kh.replay.global.config.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
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
     * JWT 필터 제외 경로
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        // 인증 이전 API
        if (uri.equals("/api/members/login")) return true;
        if (uri.equals("/api/auth/login")) return true;
        if (uri.equals("/api/auth/signUp")) return true;
        if (uri.equals("/api/members/genres")) return true;
        if (uri.equals("/members/login")) return true;
        if (uri.equals("/login")) return true;
        if (uri.equals("/api/members/signup") || uri.startsWith("/api/members/signup")) return true;

        // OAuth2
        if (uri.startsWith("/oauth2/")) return true;
        if (uri.startsWith("/login/oauth2/")) return true;
        if (uri.equals("/oauth-callback")) return true;

        // WebSocket
        if (uri.startsWith("/ws-chat")) return true;

        // 정적 리소스
        if (uri.equals("/favicon.ico")) return true;
        if (uri.equals("/error")) return true;
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/")) return true;

        // 테스트 경로
        if (uri.startsWith("/test/")) return true;

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        boolean requiresAuth =
                uri.contains("/me") ||
                uri.contains("/likes") ||
                uri.contains("/bookmarks");

        // 인증이 필요한 API인데 토큰이 없으면 즉시 차단
        if (requiresAuth && (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer "))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 없는 경우 (permitAll API)
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