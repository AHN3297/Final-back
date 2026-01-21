package com.kh.replay.global.config.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
	//필터의 주요 로직을 구현하는 메서드 , 요청이 들어올때마다 호출됨
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String uri = request.getRequestURI();
		if(uri.equals("/api/auth/members/login" ) || uri.equals("/api/auth/signUp")) {
			filterChain.doFilter(request, response);
			return;
		}
		//토큰검증
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		
		if(authorization != null && authorization.startsWith("Bearer ")) {
			String token = authorization.substring(7);
			try {
				
			Claims claims =jwtUtil.parseJwt(token);
			String username =  claims.getSubject();
			
			CustomUserDetails user = (CustomUserDetails)userDetailsService.loadUserByUsername(username);
			
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			} catch(ExpiredJwtException e) { log.info("토큰의 유효기간 만료");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;}
			
			catch(JwtException e) {
				log.info("서버에서 만들어진 토큰이 아님");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().write("유효하지 않은 토큰입니다.");
			}
			
	}
		filterChain.doFilter(request, response);

	}}
