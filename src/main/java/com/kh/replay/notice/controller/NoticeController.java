package com.kh.replay.notice.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	 * 공지사항 목록 조회
	 * - 상태(status) 기준 필터링
	 * - 제목 키워드 검색 지원
	 * - 페이징 처리
	 *
	 * 관리자 공지사항 관리 화면에서 사용하는 목록 조회 API
	 *
	 * @param page    현재 페이지 번호 (기본값: 1)
	 * @param size    페이지당 조회 개수 (기본값: 10)
	 * @param keyword 검색 키워드 (nullable)
	 * @param status  공지사항 상태 (기본값: Y)
	 * @return 공지사항 목록 및 페이징 정보
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
	 * - 관리자 전용 기능
	 * - 공지사항 제목/내용을 Form-Data 방식으로 전달
	 * - 이미지가 있을 경우 MultipartFile로 함께 업로드
	 *
	 * @param requestDto 공지사항 등록 요청 DTO (제목, 내용)
	 * @param image      첨부 이미지 파일 (nullable)
	 * @return 등록 결과 응답 (201 Created)
	 */
	@PostMapping("/register")
	public ResponseEntity<ResponseData<Void>> registerNotice(
			@ModelAttribute NoticeRequestDto requestDto, // 제목, 내용 (JSON이 아닌 Form - data 방식)
			@RequestPart(value = "images", required = false ) MultipartFile image)
	{
		
		noticeService.registerNotice(requestDto, image);
		
		// ResponseData.created는 201 상태코드 반환
		return ResponseData.created(null, "공지사항 등록 성공");
	}
	
	/**
	 * 공지사항 상세 조회
	 * - 공지사항 번호 기준 단건 조회
	 * - 공지사항 본문과 연결된 이미지 목록을 함께 반환
	 *
	 * 관리자 공지사항 관리 화면에서 상세 확인 용도
	 *
	 * @param noticeNo 공지사항 번호
	 * @return 공지사항 상세 정보
	 */
	@GetMapping("/{noticeNo}")
	public ResponseEntity<ResponseData<NoticeDetailResponseDto>> getNoticeDetail(@PathVariable("noticeNo") Long noticeNo){
		
		NoticeDetailResponseDto result = noticeService.getNoticeDetail(noticeNo);
		return ResponseData.ok(result, "공지사항 상세 조회 성공");
	}
	
	/**
	 * 공지사항 수정
	 * - 관리자 전용 기능
	 * - 공지사항 제목/내용 수정
	 * - 선택된 이미지 삭제 및 신규 이미지 추가 처리
	 *
	 * Multipart/Form-Data 방식으로 요청을 처리한다.
	 *
	 * @param noticeNo  공지사항 번호
	 * @param requestDto 공지사항 수정 요청 DTO
	 * @return 수정 결과 응답
	 */
	@PutMapping(value="/{noticeNo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ResponseData<Void>> updateNotice(
				@PathVariable("noticeNo") Long noticeNo,
				@ModelAttribute NoticeUpdateRequestDto requestDto
			){
		
		noticeService.updateNotice(noticeNo, requestDto);
		
		return ResponseData.ok(null, "공지사항 수정 성공");
		
	}

	/**
	 * 공지사항 삭제 (소프트 삭제)
	 * - 관리자 전용 기능
	 * - 공지사항 상태를 비활성화 처리
	 * - 연결된 이미지도 함께 소프트 삭제
	 *
	 * @param noticeNo 공지사항 번호
	 * @return 삭제 결과 응답
	 */
	@DeleteMapping("/{noticeNo}")
	public ResponseEntity<ResponseData<Void>> deleteNotice(@PathVariable("noticeNo") Long noticeNo){
		
		noticeService.deleteNotice(noticeNo);
		
		return ResponseData.ok(null, "공지사항 삭제 성공");
	}
}
