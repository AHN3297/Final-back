package com.kh.replay.auth.admin.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.replay.auth.admin.dao.AdminMapper;
import com.kh.replay.global.util.PageInfo;
import com.kh.replay.global.util.Pagenation;
import com.kh.replay.member.model.vo.MemberVO;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
	private final AdminMapper adminMapper;
	private final Pagenation pagenation;

	@Override
	public Map<String,Object> memberList(int page, int size) {
		//전체 회원 수 조회
		int totalElements = adminMapper.totalCount(); 
	
		//pageInfo 생성
		PageInfo pageInfo = pagenation.getPageInfo(
				totalElements,
				page, //currentPage
				size, // 페이지당 회원 수
				10 // 페이지 네비게이션 개수
	);
		int offset = (page-1)* size;
		//회원 목록 조회
		List<MemberVO> items = adminMapper.getMemberList(offset,size);
		
		//Map형태로 담아서
		Map<String,Object> response = new HashMap<>();
		response.put("page", page);
		response.put("size", size);
		response.put("totalElements", totalElements);
		response.put("totalPages", pageInfo.getMaxPage());
		response.put("startPage", pageInfo.getStartPage());
		response.put("endPage", pageInfo.getEndPage());
		response.put("items",items);
		
		return response;
		
	}
			
		
	}
	

