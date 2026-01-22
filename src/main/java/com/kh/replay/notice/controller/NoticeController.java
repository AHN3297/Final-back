package com.kh.replay.notice.controller;

import java.util.Map;

import org.apache.http.protocol.ResponseDate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.common.ResponseData;
import com.kh.replay.notice.model.dto.NoticeDetailResponseDto;
import com.kh.replay.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.notice.model.dto.NoticeRequestDto;
import com.kh.replay.notice.model.dto.NoticeUpdateRequestDto;
import com.kh.replay.notice.service.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class NoticeController {
	
	private final NoticeService noticeService;
	
	/**
	 * 공지사항 전체 조회
	 * @param page
	 * @param size
	 * @param keyword
	 * @param status
	 * @return
	 */
	@GetMapping
	public ResponseEntity<ResponseData<NoticeListResponseDto>> getNoticeList(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "status", defaultValue = "Y") String status
			){
		
		
		// 1. 서비스에서 데이터(DTO)만 받아옴
		NoticeListResponseDto result = noticeService.getNoticeList(page, size, keyword, status);
		
		// 2. ResponseData 클래스에 만들어둔 static 메서드를 사용해 포장해서 보냄.
		return ResponseData.ok(result, "관리자 공지사항 목록 조회 성공");
	}
	
	/**
	 * 공지사항 등록
	 * @param requestDto
	 * @param image
	 * @return
	 */
	@PostMapping
	public ResponseEntity<ResponseData<Void>> registerNotice(
			@ModelAttribute NoticeRequestDto requestDto, // 제목, 내용 (JSON이 아닌 Form - data 방식)
			@RequestPart(value = "images", required = false ) MultipartFile image)
	{
		
		noticeService.registerNotice(requestDto, image);
		
		// ResponseData.created는 201 상태코드 반환
		return ResponseData.created(null, "공지사항 등록 성공");
	}
	
	/**
	 * 공지사항 상세조회
	 * @param noticeNo
	 * @return
	 */
	@GetMapping("/{noticeNo}")
	public ResponseEntity<ResponseData<NoticeDetailResponseDto>> getNoticeDetail(@PathVariable("noticeNo") Long noticeNo){
		
		NoticeDetailResponseDto result = noticeService.getNoticeDetail(noticeNo);
		return ResponseData.ok(result, "공지사항 상세 조회 성공");
	}
	
	@PutMapping(value="/{noticeNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseData<Void>> updateNotice(
				@PathVariable("noticeNo") Long noticeNo,
				@ModelAttribute NoticeUpdateRequestDto requestDto
			){
		
		noticeService.updateNotice(noticeNo, requestDto);
		
		return ResponseData.ok(null, "공지사항 수정 성공");
		
	}

}
