package com.kh.replay.global.config.filter;
import java.io.IOException;
import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.kh.replay.auth.token.util.JwtUtil;
import com.kh.replay.member.model.service.UserDetailsServiceImpl;
import com.kh.replay.member.model.vo.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        // OAuth 관련 경로는 필터 제외
        if (uri.startsWith("/oauth2/") || uri.startsWith("/login/") || uri.equals("/oauth-callback")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            try {
                Claims claims = jwtUtil.parseJwt(token);
                String memberId = claims.getSubject();
                String email = claims.get("email", String.class);
                String role = claims.get("role", String.class);
                // 1. role 정제 및 기본값 설정
                if (role == null || role.trim().isEmpty()) {
                    role = "ROLE_USER";
                } else {
                    role = role.replace("[", "").replace("]", "").trim();
                }
                // OAuth 사용자인지 확인 (숫자만 있으면 소셜 로그인)
                if (memberId.startsWith("#") || memberId.matches("^[0-9]+$")) {
                    // 소셜 사용자도 CustomUserDetails 객체로 생성
                    // VO 구조(username, authorities 등)에 맞게 빌더 사용
                    CustomUserDetails socialUser = CustomUserDetails.builder()
                            .username(memberId)
                            .memberName("Social User") // 필요시 claims에서 닉네임 추출 가능
                            .authorities(Collections.singleton(new SimpleGrantedAuthority(role)))
                            .build();
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            socialUser, // Principal에 객체 전달
                            null,
                            socialUser.getAuthorities()
                        );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // 일반 로그인 사용자 - DB 조회
                    CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(memberId);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("유효하지 않은 토큰입니다.");
                return;
            }
        } else {
            log.info("No authorization header or invalid format");
        }
        filterChain.doFilter(request, response);
    }
}
