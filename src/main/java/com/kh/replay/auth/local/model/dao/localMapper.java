package com.kh.replay.auth.local.model.dao;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.member.model.vo.MemberVO;
@Mapper
public interface localMapper {
	
	int insertMember(MemberVO member);		
	int signUp(LocalDTO local);
	
	@Insert("INSERT INTO TB_MEMBER (MEMBER_ID,MEMBER_NAME,EMAIL,NAME,ROLE,STATUS) VALUES (#{memberId},#{name},{email},{role},{status}" )
	Map<String, Object> insertSocialMember(MemberVO member);

	}


