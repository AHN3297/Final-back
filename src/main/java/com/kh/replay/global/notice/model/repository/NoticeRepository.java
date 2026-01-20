package com.kh.replay.global.notice.model.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.replay.global.notice.model.domain.Notice;

@Mapper
public interface NoticeRepository {

	// 공지사항 전체 목록 조회
	List<Notice> findAll(String keyword, String status, RowBounds rowBounds);
	
	// 공지사항 상세 조회
	Notice findById(Long id);
	
	// 공지사항 작성
	int save(Notice notice);
	
	// 공지사항 수정
	int update(Notice notice);
	
	// 공지사항 삭제
	int delete(Long id);
	
	// 
	int countAll(String keyword, String status);
}
