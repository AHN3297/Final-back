package com.kh.replay.global.notice.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.notice.model.domain.Notice;
import com.kh.replay.global.notice.model.dto.NoticeItemDto;
import com.kh.replay.global.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.global.notice.model.dto.NoticeRequestDto;
import com.kh.replay.global.notice.model.repository.NoticeRepository;
import com.kh.replay.global.s3.S3Service;
import com.kh.replay.global.util.PageInfo;
import com.kh.replay.global.util.Pagenation;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
	
	private final NoticeRepository noticeRepository;
	private final Pagenation pagenation;
	private final S3Service s3Service;
	
	

	@Override
	@Transactional(readOnly = true)
	public NoticeListResponseDto getNoticeList(int page, int size, String keyword, String status) {
		
		// 1. 전체 게시글 수 조회
		int listCount = (int)noticeRepository.countAll(keyword, status);
		
		// 2. 공통 유틸을 사용 페이지 정보 계산     
		PageInfo pageInfo = pagenation.getPageInfo(listCount, page, size, 10);
		
		// 3. MaBatis RowBounds 생성
		int offset = (pageInfo.getCurrentPage() - 1) * pageInfo.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, size);
		
		// 4. DB 목록 조회
		List<Notice> notices = noticeRepository.findAll(keyword, status, rowBounds);
		

		// 5. Entity => DTO 변환
		List<NoticeItemDto> noticeItems = notices.stream().map(NoticeItemDto::new).collect(Collectors.toList());
		
		// 6. 최종 결과 반환
		return NoticeListResponseDto.builder().page(pageInfo.getCurrentPage())
											  .size(pageInfo.getBoardLimit())
											  .totalElements(pageInfo.getListCount())
											  .totalPages(pageInfo.getMaxPage())
											  .items(noticeItems)
											  .build();
	}
	
	@Override
	@Transactional
	public void registerNotice(NoticeRequestDto requestDto, MultipartFile image) throws IOException {
		
		String imgPath =  null;
		
		// 1. 이미지가 있으면
		if (image != null && !image.isEmpty()) {
			imgPath = s3Service.uploadFile(image); // S3 업로드 후 URL 반환
		}
		
		// 2. 도메인 객체 생성 (Bulider 활용)
		Notice notice = Notice.builder()
						.title(requestDto.getTitle())
						.content(requestDto.getContent())
						.imgPath(imgPath)
						.status("Y")
						.viewCount(0)
						.createdAt(LocalDateTime.now())
						.build();
		
		noticeRepository.save(notice);
	}
	
	

}
