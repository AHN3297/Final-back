package com.kh.replay.global.notice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.global.notice.model.repository.NoticeRepository;
import com.kh.replay.global.util.Pagenation;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
	
	private final NoticeRepository noticeRepository;
	private final Pagenation pagenation;
	
	

	@Override
	@Transactional(readOnly = true)
	public NoticeListResponseDto getNoticeList(int page, int size, String keyword, String status) {
		
		// 1. 전체 게시글 수 조회
		int listCount = (int)noticeRepository.getNoticeList();
		// 2. 공통 유틸을 사용 페이지 정보 계산     
		
		//
		

		return null;
	}

}
