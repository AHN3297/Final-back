package com.kh.replay.member.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kh.replay.auth.oauth.model.dto.AdditionalInfoRequest;
import com.kh.replay.auth.oauth.model.dto.OAuthUserDTO;
import com.kh.replay.member.model.dto.MemberDTO;

@Mapper
public interface MemberMapper {

	
	
	//멤버 아이디로 조회
	Map<String,String> loadByMemberEmail(String email);
	
	//JWT검증
	Map<String, String> findByMemberId(String memberId);
	
	@Update("UPDATE TB_MEMBER SET PASSWORD = #{newPassword} WHERE MEMBER_ID = #{memberId}")
	int changePassword(Map<String, String> changeRequest);
	
	@Select("SELECT MEMBER_ID memberId , MBTI mbti, MEMBER_JOB job, GENDER gender, GENRE genre, MEMBER_NAME name, NICKNAME nickName, PHONE phone, EMAIL email FROM TB_MEMBER WHERE MEMBER_ID =#{memberId}") 
	Map<String, Object> findAllInfo(String memberId);
	
	int changeInfo(MemberDTO membermember);

	@Update("UPDATE TB_MEMBER  SET STATUS = 'N' WHERE MEMBER_ID = #{memberId}")
	void withdrawMember(String memberId);

	void insertOAuthBasicInfo(OAuthUserDTO oauthUser);

	@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE EMAIL = #{email}")
	boolean existByEmail(String email);

	void updateCompleteMember(AdditionalInfoRequest request);

	
	Map<String,String> loadSocialUser(String memberId);
	
	@Update("UPDATE TB_MEMBER SET STATUS = 'N' WHERE MEMBER_ID = #{memberId}")
	void withdrawSocial(String memberId);

	
	
	
}