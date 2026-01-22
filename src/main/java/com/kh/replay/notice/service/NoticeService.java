package com.kh.replay.notice.service;

import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.notice.model.dto.NoticeDetailResponseDto;
import com.kh.replay.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.notice.model.dto.NoticeRequestDto;

public interface NoticeService {

	// 공지사항 목록 조회
	NoticeListResponseDto getNoticeList(int page, int size, String keyword, String status);

	// 공지사항 등록
	void registerNotice(NoticeRequestDto requestDto, MultipartFile image);

	// 공지사항 상세 조회
	NoticeDetailResponseDto getNoticeDetail(Long noticeNo);
}
