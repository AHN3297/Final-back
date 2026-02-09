package com.kh.replay.auth.local.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.member.model.vo.MemberVO;

@Mapper
public interface LocalMapper {

	int insertMember(MemberVO member);

	@Insert("INSERT INTO TB_MEMBER_GENRE (MEMBER_GENRE_NO , GENRE_ID , MEMBER_ID) VALUES(SEQ_MEMBER_GENRE_NO.NEXTVAL,#{genreId},#{memberId})")
	int insertOneMemberGenre(@Param("memberId") String memberId, @Param("genreId") Long genreId);

	int signUp(LocalDTO local);

	@Insert("INSERT INTO TB_MEMBER (MEMBER_ID,MEMBER_NAME,EMAIL,NICKNAME,ROLE,STATUS) VALUES (#{memberId},#{name},{email},#{nickName}.#{role},#{status}")
	Map<String, Object> insertSocialMember(MemberVO member);

}
