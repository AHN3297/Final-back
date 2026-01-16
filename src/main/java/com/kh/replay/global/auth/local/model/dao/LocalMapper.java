package com.kh.replay.global.auth.local.model.dao;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.global.member.model.vo.MemberVO;
@Mapper
public interface LocalMapper {
	@Insert("INSERT INTO TB_LOCAL (MEMBERID,PASSWORD) VALUES (#{memberId},#{password})")

	 int signUp(MemberVO member);		
		
		

	}


