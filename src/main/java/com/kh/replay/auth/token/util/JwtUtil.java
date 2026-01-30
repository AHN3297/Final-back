package com.kh.replay.auth.token.util;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    private SecretKey key;
    
    @PostConstruct
    public void init() {
        log.info("{}", secretKey);
        byte[] arr = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(arr);
    }
    
    // 기존 메서드 (일반 로그인용)
    public String getAccessToken(String username,String role) {
        return Jwts.builder()
                    .subject(username)
                    .claim("role", role)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
                    .signWith(key)
                    .compact();
    }
    
    // OAuth 로그인용 메서드 추가
    public String getAccessToken(String memberId, String email, String role ,String name) {
        return Jwts.builder()
                    .subject(memberId)                    // memberId를 subject로
                    .claim("email", email)                // email 추가
                    .claim("role", role)                  // role 추가
                    .claim("name",name)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))  // 24시간
                    .signWith(key)
                    .compact();
    }
    
    public String getRefreshToken(String username) {
        return Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(Date.from(Instant.now().plus(Duration.ofDays(14))))
                    .signWith(key)
                    .compact();
    }
    
    public Claims parseJwt(String token) {
        return Jwts.parser()	
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }
    
    public String getMemberIdFromToken(String token) {
        return parseJwt(token).getSubject();
    }
    
    public String getEmailFromToken(String token) {
        return parseJwt(token).get("email", String.class);
    }
    
    public String getRoleFromToken(String token) {
        return parseJwt(token).get("role", String.class);
    }
}
