package com.kh.replay.auth.admin.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.kh.replay.member.model.vo.MemberVO;

@Mapper
public interface AdminMapper {			
		@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID IS NOT NULL AND STATUS = 'Y' ")
		int totalCount();
		
		
		List<MemberVO> getMemberList(int offset, int size);

		

	}


