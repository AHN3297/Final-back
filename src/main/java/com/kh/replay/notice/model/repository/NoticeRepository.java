package com.kh.replay.notice.model.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.replay.notice.model.domain.Notice;
import com.kh.replay.notice.model.domain.NoticeImg;
import com.kh.replay.notice.model.dto.NoticeUpdateRequestDto;

/**
 * 공지사항 관련 DB 접근을 담당하는 MyBatis Mapper 인터페이스
 * - 공지사항 CRUD
 * - 공지사항 이미지 관리
 * - 목록 조회 및 페이징 처리
 */
@Mapper
public interface NoticeRepository {

	/**
     * 공지사항 목록 조회
     * - 상태(status) 기준 필터링
     * - 제목 키워드 검색 지원
     * - OFFSET / LIMIT 기반 페이징 처리
     *
     * @param keyword 검색 키워드 (nullable)
     * @param status  공지사항 상태 (Y/N)
     * @param offset  조회 시작 위치
     * @param limit   조회 개수
     * @return 공지사항 엔티티 목록
     */
	List<Notice> findAll(@Param("keyword") String keyword,@Param("status") String status,@Param("offset") int offset, @Param("limit")int limit);
	
	/**
     * 공지사항 전체 개수 조회
     * - 목록 페이징 처리를 위한 total count
     * - 목록 조회 조건과 동일한 필터 적용
     *
     * @param keyword 검색 키워드 (nullable)
     * @param status  공지사항 상태 (Y/N)
     * @return 공지사항 총 개수
     */
	int countAll(@Param("keyword") String keyword, @Param("status") String status);

	/**
     * 공지사항 단건 조회
     * - 공지사항 번호 기준 조회
     *
     * @param noticeNo 공지사항 번호
     * @return 공지사항 엔티티 (없을 경우 null)
     */
	Notice findByNoticeNo(Long noticeNo);
	
	/**
     * 특정 공지사항에 등록된 이미지 URL 목록 조회
     * - 활성 상태(STATUS = 'Y')인 이미지만 조회
     *
     * @param noticeNo 공지사항 번호
     * @return 이미지 URL 목록
     */
	List<String> findImageUrlsByNoticeNo(Long noticeNo);
	
	/**
     * 공지사항 등록
     *
     * @param notice 공지사항 엔티티
     * @return 처리된 행 수
     */
	int save(Notice notice);
	
	/**
     * 공지사항 이미지 등록
     *
     * @param noticeImg 공지사항 이미지 엔티티
     * @return 처리된 행 수
     */
	int saveImg(NoticeImg noticeImg);

	/**
     * 공지사항 본문 수정
     * - 제목, 내용 수정
     * - UPDATED_AT 갱신
     *
     * @param noticeNo 공지사항 번호
     * @param req      수정 요청 DTO
     * @return 처리된 행 수
     */
	int updateNotice(@Param("noticeNo") Long noticeNo,
					 @Param("req") NoticeUpdateRequestDto req);
	
	/**
     * 공지사항 이미지 소프트 삭제
     * - IMG_ID 목록 기준
     * - STATUS 값을 'Y' → 'N'으로 변경
     *
     * @param imgIds 삭제할 이미지 ID 목록
     * @return 처리된 행 수
     */
	int deleteNoticeImages(List<Long> imgIds);
	
	/**
     * 공지사항 이미지 추가
     * - 공지사항 수정 시 신규 이미지 등록 용도
     *
     * @param noticeNo   공지사항 번호
     * @param originName 원본 파일명
     * @param changeName 저장된 파일명(S3 URL)
     * @return 처리된 행 수
     */
	int insertNoticeImage(@Param("noticeNo") Long noticeNo,
						  @Param("originName") String originName,
						  @Param("changeName") String changeName);
	
	/**
     * 공지사항 소프트 삭제
     * - STATUS 값을 'Y' → 'N'으로 변경
     *
     * @param noticeNo 공지사항 번호
     * @return 처리된 행 수
     */
	int deleteNotice(Long noticeNo);
	
	 /**
     * 특정 공지사항에 연결된 이미지 ID 목록 조회
     * - 이미지 삭제 처리를 위한 사전 조회
     *
     * @param noticeNo 공지사항 번호
     * @return 이미지 ID 목록
     */
	List<Long> findImgIdsByNoticeNo(Long noticeNo);
	
}
