package com.kh.replay.notice.model.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.kh.replay.notice.model.domain.Notice;
import com.kh.replay.notice.model.domain.NoticeImg;

@Mapper
public interface NoticeRepository {

	// 공지사항 전체 목록 조회
	List<Notice> findAll(@Param("keyword") String keyword,@Param("status") String status,@Param("offset") int offset, @Param("limit")int limit);
	
	// 공지사항 상세 조회
	Notice findById(Long id);
	
	// 공지사항 작성
	int save(Notice notice);
	
	// 공지사항 수정
	int update(Notice notice);
	
	// 공지사항 삭제
	int delete(Long id);
	
	int countAll(@Param("keyword") String keyword, @Param("status") String status);

	int saveImg(NoticeImg noticeImg);
}
