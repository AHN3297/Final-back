package com.kh.replay.auth.local.model.dao;
import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.auth.member.model.vo.MemberVO;
@Mapper
public interface LocalMapper {
	
	int insertMember(MemberVO member);		
	int signUp(LocalDTO local);
		
		

	}


