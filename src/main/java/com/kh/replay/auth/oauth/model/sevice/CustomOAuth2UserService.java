package com.kh.replay.auth.oauth.model.sevice;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.kh.replay.auth.oauth.model.dao.SocialMapper;
import com.kh.replay.auth.oauth.model.dto.OAuthUserDTO;
import com.kh.replay.auth.oauth.model.res.GoogleRes;
import com.kh.replay.auth.oauth.model.res.OAuth2Res;
import com.kh.replay.auth.oauth.model.vo.CustomOAuth2User;
import com.kh.replay.global.exception.OAuth2AuthenticationException;
import com.kh.replay.member.model.dao.MemberMapper;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService{
	private final SocialMapper socialMapper;
	private final MemberMapper membermapper;
	
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
		 OAuth2User oAuth2User = super.loadUser(userRequest);

	        String registrationId = userRequest.getClientRegistration().getRegistrationId();
	        OAuth2Res oAuth2Response = switch (registrationId) {
	            case "google" -> new GoogleRes(oAuth2User.getAttributes());
	            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
	        };

	       String memberId = "#"+oAuth2Response.getProviderId();
	       String email = oAuth2Response.getEmail();
	       
	       boolean isLocalUser = membermapper.existByEmail(email);
	       if(isLocalUser) {
	    	   throw new OAuth2AuthenticationException("이미 일반 회원으로 가입된 이메일 입니다.");
	       }
	       
	        // 2) OAuth2 응답 기반 DTO 생성
	        OAuthUserDTO oauthUser = new OAuthUserDTO(
	        	    memberId,
	        	    oAuth2Response.getProvier(),
	        	    oAuth2Response.getProviderId(),
	        	    null,  // createdAt
	        	    oAuth2Response.getEmail(),
	        	    oAuth2Response.getName(),
	        	    false  // isNewUser
	        	);
	        //제공자와 제공자 ID로 유저 조회 한다
	        OAuthUserDTO existingUser = socialMapper.findByProviderAndProviderId(oauthUser);
	        if(existingUser !=null) {
	        	existingUser.setNewUser(false);
	        	existingUser.setEmail(oAuth2Response.getEmail());
	        	existingUser.setName(oAuth2Response.getName());
	        	return new CustomOAuth2User(existingUser);
	        }
	        //멤버테이블에 EMAIL과 NAME+ 임시값
	        membermapper.insertOAuthBasicInfo(oauthUser);
	        // 신규회원 social테이블에 추가
	        socialMapper.insertOAuthUser(oauthUser);
	        oauthUser.setNewUser(true);
	        
			return new CustomOAuth2User(oauthUser);
	
	
	
	}
	

}
