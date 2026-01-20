package com.kh.replay.global.notice.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.global.notice.model.dto.NoticeRequestDto;

public interface NoticeService {

	// 공지사항 목록 조회
	NoticeListResponseDto getNoticeList(int page, int size, String keyword, String status);

	void registerNotice(NoticeRequestDto requestDto, MultipartFile image) throws IOException;

}
