package com.kh.replay.auth.admin.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.PageRequest;

import com.kh.replay.auth.admin.model.dto.PageRequestDTO;
import com.kh.replay.member.model.dto.MemberDTO;
import com.kh.replay.member.model.vo.MemberVO;

@Mapper
public interface AdminMapper {			
		@Select("SELECT COUNT(*) FROM TB_MEMBER WHERE MEMBER_ID IS NOT NULL AND STATUS = 'Y' ")
		int totalCount();
		
		


		List<MemberDTO> getMemberList(PageRequestDTO pageRequest);

		

	}


