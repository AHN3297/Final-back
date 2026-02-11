package com.kh.replay.notice.service;

import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.notice.model.dto.NoticeDetailResponseDto;
import com.kh.replay.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.notice.model.dto.NoticeRequestDto;
import com.kh.replay.notice.model.dto.NoticeUpdateRequestDto;

/**
 * 공지사항 비즈니스 로직을 정의하는 Service 인터페이스
 * - 공지사항 CRUD
 * - 관리자 권한 검증
 * - 이미지 업로드 및 관리
 */
public interface NoticeService {

	/**
     * 공지사항 목록 조회
     * - 상태(status) 기준 필터링
     * - 제목 키워드 검색 지원
     * - 페이징 처리
     *
     * @param page    현재 페이지 번호 (1부터 시작)
     * @param size    페이지당 조회 개수
     * @param keyword 검색 키워드 (nullable)
     * @param status  공지사항 상태 (Y/N)
     * @return 공지사항 목록 및 페이징 정보 DTO
     */
	NoticeListResponseDto getNoticeList(int page, int size, String keyword, String status);

	/**
     * 공지사항 등록
     * - 관리자 권한 필요
     * - 공지사항 본문 저장
     * - 이미지가 있을 경우 S3 업로드 후 별도 테이블에 저장
     *
     * @param requestDto 공지사항 등록 요청 DTO
     * @param image      첨부 이미지 파일 (nullable)
     */
	void registerNotice(NoticeRequestDto requestDto, MultipartFile image);

	/**
     * 공지사항 상세 조회
     * - 활성 상태(Y) 공지사항만 조회 가능
     * - 공지사항 본문 + 이미지 목록 반환
     *
     * @param noticeNo 공지사항 번호
     * @return 공지사항 상세 정보 DTO
     */
	NoticeDetailResponseDto getNoticeDetail(Long noticeNo);
	
	/**
     * 공지사항 수정
     * - 관리자 권한 필요
     * - 공지사항 본문 수정
     * - 이미지 삭제 및 신규 이미지 추가 처리
     *
     * @param noticeNo   공지사항 번호
     * @param requestDto 공지사항 수정 요청 DTO
     */
	void updateNotice(Long noticeNo, NoticeUpdateRequestDto requestDto);
	
	/**
     * 공지사항 삭제 (소프트 삭제)
     * - 관리자 권한 필요
     * - 공지사항 및 연결된 이미지 상태값 변경
     *
     * @param noticeNo 공지사항 번호
     */
	void deleteNotice(Long noticeNo);
}
