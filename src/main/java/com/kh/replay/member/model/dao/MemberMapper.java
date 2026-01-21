package com.kh.replay.member.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kh.replay.member.model.dto.MemberDTO;

@Mapper
public interface MemberMapper {

	
	//멤버 아이디로 조회
	Map<String,String> loadUser(String memberId);
	
	//멤버 이메일로 로그인
	Map<String,String> loadByMemberId(String email);

	@Update("UPDATE TB_MEMBER SET PASSWORD = #{newPassword} WHERE MEMBER_ID = #{memberId}")
	int changePassword(Map<String, String> changeRequest);
	
	@Select("SELECT MEMBER_ID	, MBTI, MEMBER_JOB, GENDER , GENRE, MEMBER_NAME, NICKNAME, PHONE, EMAIL FROM TB_MEMBER WHERE MEMBER_ID =#{memberId}") 
	Map<String, Object> findAllMember(String memberId);
	
	int changeInfo(MemberDTO membermember);

	@Delete("UPDATE TB_MEMBER  SET STATUS = 'N' WHERE MEMBER_ID = #{memberId}")
	void withdrawMember(String memberId);
	
	
}