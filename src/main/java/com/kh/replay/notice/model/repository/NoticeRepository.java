package com.kh.replay.notice.model.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.replay.notice.model.domain.Notice;
import com.kh.replay.notice.model.domain.NoticeImg;
import com.kh.replay.notice.model.dto.NoticeUpdateRequestDto;

@Mapper
public interface NoticeRepository {

	// 공지사항 전체 목록 조회
	List<Notice> findAll(@Param("keyword") String keyword,@Param("status") String status,@Param("offset") int offset, @Param("limit")int limit);
	
	int countAll(@Param("keyword") String keyword, @Param("status") String status);

	// 공지사항 상세 조회
	Notice findByNoticeNo(Long noticeNo);
	
	List<String> findImageUrlsByNoticeNo(Long noticeNo);
	
	// 공지사항 작성
	int save(Notice notice);
	
	int saveImg(NoticeImg noticeImg);

	// 공지사항 수정
	int updateNotice(@Param("noticeNo") Long noticeNo,
					 @Param("req") NoticeUpdateRequestDto req);
	
	// 이미지 삭제(소프트)
	int deleteNoticeImages(List<Long> imgIds);
	
	int insertNoticeImage(@Param("noticeNo") Long noticeNo,
						  @Param("originName") String originName,
						  @Param("changeName") String changeName);
	
	// 공지사항 삭제(소프트 삭제)
	int deleteNotice(Long noticeNo);
	
	// 해당 공지 이미지 ID 목록
	List<Long> findImgIdsByNoticeNo(Long noticeNo);
	
}
