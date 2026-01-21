package com.kh.replay.auth.token.model.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.auth.token.model.vo.RefreshToken;

@Mapper
public interface TokenMapper {

	@Insert("INSERT INTO TB_TOKEN (TOKEN_ID,TOKEN,EXPIRATION,CREATED_AT,MEMBER_ID) VALUES(SEQ_TOKEN_ID.NEXTVAL,#{token},#{expiration},#{createdAt},#{memberId})")	
	int saveToken(RefreshToken token);

	@Delete("DELETE FROM TB_TOKEN WHERE MEMBER_ID =#{memberId}")
	void memberLogout(String memberId);


	
	
}
