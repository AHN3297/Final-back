package com.kh.replay.global.notice.service;

import com.kh.replay.global.notice.model.dto.NoticeListResponseDto;

public interface NoticeService {

	// 공지사항 목록 조회
	NoticeListResponseDto getNoticeList(int page, int size, String keyword, String status);

}
