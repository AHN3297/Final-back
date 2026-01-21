package com.kh.replay.member.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kh.replay.member.model.dto.MemberDTO;

@Mapper
public interface MemberMapper {

	@Select("SELECT MEMBER_ID FROM TB_MEMBER")
	int CountMemberId(String memberId);
	
	@Select("SELECT PASSWORD FROM TB_LOCAL")
	int CountPassword(String password);
	
	
	Map<String,String> loadUser(String memberId);

	@Update("UPDATE TB_MEMBER SET PASSWORD = #{newPassword} WHERE MEMBER_ID = #{memberId}")
	int changePassword(Map<String, String> changeRequest);
	
	@Select("SELECT MEMBER_ID	, MBTI, MEMBER_JOB, GENDER , GENRE, MEMBER_NAME, NICKNAME, PHONE, EMAIL FROM TB_MEMBER WHERE MEMBER_ID =#{memberId}") 
	Map<String, Object> findAllMember(String memberId);
	
	int changeInfo(MemberDTO membermember);

	@Delete("UPDATE TB_MEMBER  SET STATUS = 'N' WHERER MEMBER_ID = #{memberId}")
	void wirhdrawMember(String memberId);
	
	
}