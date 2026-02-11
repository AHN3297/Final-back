package com.kh.replay.auth.token.model.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.replay.auth.token.model.dao.TokenMapper;
import com.kh.replay.auth.token.model.vo.RefreshToken;
import com.kh.replay.auth.token.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Slf4j
@Service
public class TokenService {
private final JwtUtil tokenUtil;
private final TokenMapper tokenMapper;
public Map<String,String> generateToken(String memberId,String role){
	
 Map<String ,String> tokens = createTokens(memberId,role );


saveToken(tokens.get("refreshToken"),memberId); 

	
	return tokens;

}
	private Map<String,String> createTokens( String memberId,String role) {
		String accessToken = tokenUtil.getAccessToken(memberId,role);
	String refreshToken =tokenUtil.getRefreshToken(memberId);
	 Map<String,String> tokens = new HashMap();
		 tokens.put("accessToken", accessToken);
	 tokens.put("refreshToken", refreshToken);
	return tokens;
}
	private void saveToken(String refreshToken, String memberId) {
		RefreshToken token = RefreshToken.builder()
				  .token(refreshToken)
				  .memberId(memberId)
				  .expiration(new Date(System.currentTimeMillis()+3600000L*72))
				  .createdAt(new Date())
				  .build();		
		tokenMapper.saveToken(token);		
		
	}
}