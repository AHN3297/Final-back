package com.kh.replay.notice.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.exception.FileUploadException;
import com.kh.replay.global.exception.ResourceNotFoundException;
import com.kh.replay.global.s3.S3Service;
import com.kh.replay.global.util.PageInfo;
import com.kh.replay.global.util.Pagenation;
import com.kh.replay.notice.model.domain.Notice;
import com.kh.replay.notice.model.domain.NoticeImg;
import com.kh.replay.notice.model.dto.NoticeDetailResponseDto;
import com.kh.replay.notice.model.dto.NoticeItemDto;
import com.kh.replay.notice.model.dto.NoticeListResponseDto;
import com.kh.replay.notice.model.dto.NoticeRequestDto;
import com.kh.replay.notice.model.dto.NoticeUpdateRequestDto;
import com.kh.replay.notice.model.repository.NoticeRepository;

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
		int limit = pageInfo.getBoardLimit();
		
		// 4. DB 목록 조회
		List<Notice> notices = noticeRepository.findAll(keyword, status, offset, limit);
		

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
	public void registerNotice(NoticeRequestDto requestDto, MultipartFile image) {
		
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
			
			try {
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
			
			} catch (Exception e) {
				throw new FileUploadException("이미지 업로드에 실패했습니다.", e);
			}
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public NoticeDetailResponseDto getNoticeDetail(Long noticeNo) {
		
		// 1. 본문 조회
		Notice notice = noticeRepository.findByNoticeNo(noticeNo);
		
		if (notice == null) {
			throw new ResourceNotFoundException("공지사항을 찾을수 업습니다. noticeNo=" + noticeNo);
		}
		
		// Status "Y"만 노출
		if (!"Y".equals(notice.getStatus())) {
			throw new ResourceNotFoundException("비활성화된 공지사항입니다. notice=" + noticeNo);
		}
		
		// 2. 이미지 조회
		List<String> images = noticeRepository.findImageUrlsByNoticeNo(noticeNo);
		
		// 3. DTO 조립
		return NoticeDetailResponseDto.builder()
					.noticeNo(notice.getNoticeNo())
					.title(notice.getNoticeTitle())
					.content(notice.getNoticeContent())
					.memberId(notice.getMemberId())
					.status(notice.getStatus())
					.images(images)
					.build();
	}
	
	@Transactional
	public void updateNotice(Long noticeNo, NoticeUpdateRequestDto requestDto) {
		
		// 1. 공지사항 확인
		Notice origin = noticeRepository.findByNoticeNo(noticeNo);
		if(origin == null) throw new ResourceNotFoundException("공지사항을 찾을수 없습니다. noticeNo=" + noticeNo);
		
		 
		// 2. 본문 수정
		noticeRepository.updateNotice(noticeNo, requestDto);
		
		// 3. 이미지 삭제(status 'Y' => 'N')
		String deleteImgIds = requestDto.getDeleteImgIds();
		if(deleteImgIds != null && !deleteImgIds.isBlank()) {
			List<Long> ids = Arrays.stream(deleteImgIds.split(","))
						.map(String::trim)
						.filter(s -> !s.isEmpty())
						.map(Long::valueOf)
						.toList();
			if(!ids.isEmpty()) noticeRepository.deleteNoticeImages(ids); 
		}
		
		// 4. 새 이미지 추가
		List<MultipartFile> files = requestDto.getFiles();
		if(files != null && !files.isEmpty()) {
	        for(MultipartFile file : files) {
	            if(file == null || file.isEmpty()) continue;
	            
	            try {
	            	String url = s3Service.uploadFile(file);
	            	noticeRepository.insertNoticeImage(noticeNo, file.getOriginalFilename(), url);
	        
	            } catch (IOException e) {
	            	throw new FileUploadException("이미지 업로드에 실패했습니다.", e);
	            }
	        }
	    }
	}
	
	@Transactional
	public void deleteNotice(Long noticeNo) {
		
		// 1. 공지사항 확인
		Notice origin = noticeRepository.findByNoticeNo(noticeNo);
		if(origin == null) {
			throw new ResourceNotFoundException("공지사항을 찾을 수 없습니다. noticeNo=" + noticeNo);
		}
		
		// 2. 공지사항 소프트 삭제
		noticeRepository.deleteNotice(noticeNo);
		
		// 3. 해당 공지사항 이미지 소프트 삭제 (Status 'Y' -> 'N')
		List<Long> imgIds = noticeRepository.findImgIdsByNoticeNo(noticeNo);
		if(imgIds != null && !imgIds.isEmpty()) {
			noticeRepository.deleteNoticeImages(imgIds);
		}
	}

}
