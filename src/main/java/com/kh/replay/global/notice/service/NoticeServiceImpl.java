package com.kh.replay.global.notice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.notice.model.domain.Notice;
import com.kh.replay.global.notice.model.dto.NoticeItemDto;
import com.kh.replay.global.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.global.notice.model.repository.NoticeRepository;
import com.kh.replay.global.util.PageInfo;
import com.kh.replay.global.util.Pagenation;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
	
	private final NoticeRepository noticeRepository;
	private final Pagenation pagenation;
	
	

	@Override
	@Transactional(readOnly = true)
	public NoticeListResponseDto getNoticeList(int page, int size, String keyword, String status) {
		
		// 1. 전체 게시글 수 조회
		int listCount = (int)noticeRepository.countAll(keyword, status);
		// 2. 공통 유틸을 사용 페이지 정보 계산     
		PageInfo pageInfo = pagenation.getPageInfo(listCount, page, listCount, 10);
		// 3. MaBatis RowBounds 생성
		int offset = (pageInfo.getCurrentPage() - 1) * pageInfo.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, size);
		
		// 4. DB 목록 조회
		List<Notice> notices = noticeRepository.findAll(keyword, status, rowBounds);
		

		// 5. Entity => DTO 변환
		List<NoticeDto> noticeItems = notices.stream().map(NoticeItemDto::new).collect(Collectors.toList());
		
		// 6. 최종 결과 반환
		return NoticeListResponseDto.builder().page(pageInfo.getCurrentPage())
											  .size(pageInfo.getBoardLimit())
											  .totalElements(pageInfo.getListCount())
											  .totalPages(pageInfo.getMaxPage())
											  .items(noticeItems)
											  .build();
	}

}
