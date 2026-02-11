package com.kh.replay.auth.oauth.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.auth.oauth.model.dto.OAuthUserDTO;

@Mapper
public interface SocialMapper {
	OAuthUserDTO findByProviderAndProviderId(OAuthUserDTO oauthUser);

	void insertOAuthUser(OAuthUserDTO oauthUser);

	
	


	


}
