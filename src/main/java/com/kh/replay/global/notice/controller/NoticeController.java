package com.kh.replay.global.notice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.global.common.ResponseData;
import com.kh.replay.global.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.global.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeController {
	
	private final NoticeService noticeService;
	
	@GetMapping
	public ResponseEntity<ResponseData<NoticeListResponseDto>> getNoticeList(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "Y") String status
			){
		
		
		// 1. 서비스에서 데이터(DTO)만 받아옴
		NoticeListResponseDto result = noticeService.getNoticeList(page, size, keyword, status);
		
		// 2. ResponseData 클래스에 만들어둔 static 메서드를 사용해 포장해서 보냄.
		return ResponseData.ok(result, "관리자 공지사항 목록 조회 성공");
	}
	

}
