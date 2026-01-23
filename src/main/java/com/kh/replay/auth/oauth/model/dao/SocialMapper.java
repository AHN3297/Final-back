package com.kh.replay.auth.oauth.model.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kh.replay.auth.oauth.model.dto.OAuthUserDTO;

@Mapper
public interface SocialMapper {
	@Select("SELECT PROVIDER, PROVIDER_ID,CREATED_AT , MEMBER_ID FROM TB_SOCIAL WHERE PROVIDER=#{provider} AND PROVIDER_ID=#{providerId}")
	OAuthUserDTO findByProviderAndProviderId(OAuthUserDTO oauthUser);

	@Insert("INSERT INTO TB_SOCIAL (PROVIDER , PROVIDER_ID , CREATED_AT ,MEMBER_ID) VALUES(#{provider},#{providerId}, SYSDATE, #{memberId})")
	void insertOAuthUser(OAuthUserDTO oauthUser);

	


}
