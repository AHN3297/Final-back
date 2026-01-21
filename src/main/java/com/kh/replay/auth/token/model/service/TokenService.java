package com.kh.replay.auth.token.model.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.replay.auth.token.model.dao.TokenMapper;
import com.kh.replay.auth.token.util.JwtUtil;
import com.kh.replay.auth.token.model.vo.RefreshToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Slf4j
@Service
public class TokenService {
private final JwtUtil tokenUtil;
private final TokenMapper tokenMapper;
	//인증에 성공했을 때 
	//JwtUtill에 정의 해놓은 액세스랑 리프레쉬 호루 해서 담아서 login메서드로 반환
public Map<String,String> generateToken(String memberName){
	
 Map<String ,String> tokens = createTokens(memberName );


saveToken(tokens.get( "refreshToken"),memberName); 

	
	return tokens;

}
	private Map<String,String> createTokens( String memberName) {
		String accessToken = tokenUtil.getAccessToken(memberName);
	String refreshToken =tokenUtil.getRefreshToken(memberName);
	 Map<String,String> tokens = new HashMap();
		 tokens.put("accessToken", accessToken);
	 tokens.put("refreshToken", refreshToken);
	return tokens;
}
	private void saveToken(String refreshToken, String memberName) {
		RefreshToken token = RefreshToken.builder()
				  .token(refreshToken)
				  .memberId(memberName)
				  .expiration(new Date(System.currentTimeMillis()+3600000L*72))
				  .createdAt(new Date())
				  .build();		
		log.info("이거 왔나용?: {}" , memberName);
		tokenMapper.saveToken(token);		
		
	}
}