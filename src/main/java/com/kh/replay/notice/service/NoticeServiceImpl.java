package com.kh.replay.notice.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.notice.model.domain.Notice;
import com.kh.replay.notice.model.domain.NoticeImg;
import com.kh.replay.notice.model.dto.NoticeItemDto;
import com.kh.replay.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.notice.model.dto.NoticeRequestDto;
import com.kh.replay.notice.model.repository.NoticeRepository;
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
		
		// 1. 공지사항 본문(Notice) 객체 생성
		Notice notice = Notice.builder()
						.noticeTitle(requestDto.getTitle())
						.noticeContent(requestDto.getContent())
						.memberId("user1") // currentMemberId 나중에 교체
						.status("Y")
						.build();
		
		// 2. 공지사항 저장
		noticeRepository.save(notice);
		
		// 3. 이미지가 있으면 업로드 후 별도 테이블(NoticeImg)에 저장
		if (image != null && !image.isEmpty()) {
			// S3 업로드
			String s3Url = s3Service.uploadFile(image); 
			
			// 이미지 도메인 객체 생성
			NoticeImg noticeImg = NoticeImg.builder()
					.originName(image.getOriginalFilename()) // 원본명
					.changeName(s3Url)                       // S3 주소
					.noticeNo(notice.getNoticeNo())          // 위 2번에서 생성된 번호를 가져와서 연결
					.build();
			
			// 이미지 테이블에 저장
			noticeRepository.saveImg(noticeImg);
		}
	}
	
	

}
