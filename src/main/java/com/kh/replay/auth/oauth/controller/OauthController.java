package com.kh.replay.auth.oauth.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.auth.oauth.model.dto.AdditionalInfoRequest;
import com.kh.replay.auth.token.util.JwtUtil;
import com.kh.replay.global.common.ResponseData;
import com.kh.replay.member.model.dao.memberMapper;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/api/oauth/social")
@RequiredArgsConstructor
public class OauthController {
	private final memberMapper membermapper;
	private final JwtUtil jwtUtil;
	
	@PutMapping("/complete")
	public ResponseEntity<ResponseData<AdditionalInfoRequest>> addsocialInfo(
			@RequestBody AdditionalInfoRequest request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization){
				String token = authorization.substring(7); 
		        Claims claims = jwtUtil.parseJwt(token);
		        
		        String memberId = claims.getSubject();
		        String email = claims.get("email", String.class);
		        String name = claims.get("name", String.class); 
		
		
		        request.setMemberId(memberId);
		        request.setEmail(email);
		        request.setName(name);
		//멤버 테이블에 나머지 정보 저장
		membermapper.updateCompleteMember(request);
		        
		return ResponseData.created(request, "회원가입이 성공하셨습니다.");
	}
	
	
	
}
