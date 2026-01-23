package com.kh.replay.member.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kh.replay.auth.oauth.model.dto.AdditionalInfoRequest;
import com.kh.replay.auth.oauth.model.dto.OAuthUserDTO;
import com.kh.replay.member.model.dto.MemberDTO;

@Mapper
public interface memberMapper {

	
	//멤버 아이디로 조회
	Map<String,String> loadUser(String memberId);
	
	//멤버 이메일로 로그인
	Map<String,String> loadByMemberId(String email);

	@Update("UPDATE TB_LOCAL SET PASSWORD = #{newPassword} WHERE MEMBER_ID = #{memberId}")
	int changePassword(Map<String, String> changeRequest);
	
	@Select("SELECT MEMBER_ID	, MBTI, MEMBER_JOB, GENDER , GENRE, MEMBER_NAME, NICKNAME, PHONE, EMAIL FROM TB_MEMBER WHERE MEMBER_ID =#{memberId}") 
	Map<String, Object> findAllMember(String memberId);
	
	int changeInfo(MemberDTO membermember);

	@Delete("UPDATE TB_MEMBER  SET STATUS = 'N' WHERE MEMBER_ID = #{memberId}")
	void withdrawMember(String memberId);

	@Update("UPDATE TB_MEMBER SET NICKNAME = #{nickName}, PHONE = #{phone}, MBTI = #{mbti},	  MEMBER_JOB = #{job},GENRE = #{genre}, GENDER = #{gender}, UPDATED_AT = SYSDATE	 WHERE MEMBER_ID = #{memberId}")
	void updateCompleteMember(AdditionalInfoRequest request);

	@Insert("INSERT INTO TB_MEMBER (MEMBER_ID ,EMAIL,MEMBER_NAME ,NICKNAME, PHONE,MBTI,MEMBER_JOB,GENRE,GENDER)VALUES(#{memberId},#{email},#{name},'TEMP','TEMP','UNKNOWN','UNKNOWN','UNKNOWN','U')")
	void insertOAuthBasicInfo(OAuthUserDTO oauthUser);

	
	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE EMAIL = #{email}")
	boolean existByEmail(String email) ;
		
	

	
	
}