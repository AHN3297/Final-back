package com.kh.replay.notice.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
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



/**
 * 공지사항 도메인의 비즈니스 로직을 담당하는 Service 구현체
 * - 공지사항 CRUD 처리
 * - 관리자 권한 검증
 * - 공지사항 상태(활성/비활성) 검증
 * - 이미지 업로드(S3) 및 이미지 상태 관리
 *
 * Controller → Service → Repository 구조에서
 * Service 계층이 비즈니스 규칙의 중심 역할을 수행한다.
 */
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {
	
	/** 공지사항 및 이미지 관련 DB 접근을 담당하는 Repository */
	private final NoticeRepository noticeRepository;
	/** 공통 페이징 계산 유틸 */
	private final Pagenation pagenation;
	/** 공지사항 이미지 업로드를 위한 S3 서비스 */
	private final S3Service s3Service;
	
	/**
	 * 공지사항 존재 여부 및 활성 상태를 검증한다.
	 * - 공지사항이 존재하지 않거나
	 * - 상태가 비활성(Y가 아닌 경우)
	 * 위 조건 중 하나라도 만족하지 않으면 예외를 발생시킨다.
	 *
	 * @param noticeNo 공지사항 번호
	 * @return 활성 상태의 공지사항 엔티티
	 */
	private Notice getActiveNoticeOrThrow(Long noticeNo) {
		
		Notice notice = noticeRepository.findByNoticeNo(noticeNo);
		
		if(notice == null) {
			throw new ResourceNotFoundException("공지사항을 찾을 수 없습니다. noticeNo=" + noticeNo);
		}
		if(!"Y".equals(notice.getStatus())) {
			throw new ResourceNotFoundException("비활성화된 공지사항입니다. noticeNo=" + noticeNo);
		}
		
		return notice;
	}
	
	/**
	 * 공지사항 목록 조회
	 * - 상태(status) 기준 필터링
	 * - 제목 키워드 검색 지원
	 * - 페이징 처리
	 *
	 * 관리자/일반 조회 공통으로 사용되는 목록 조회 로직
	 *
	 * @param page    현재 페이지 번호 (1부터 시작)
	 * @param size    페이지당 조회 개수
	 * @param keyword 검색 키워드 (nullable)
	 * @param status  공지사항 상태 (Y/N)
	 * @return 공지사항 목록 및 페이징 정보 DTO
	 */
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
	
	
	/**
	 * 공지사항 등록
	 * - 관리자 권한 필요
	 * - 공지사항 본문 저장
	 * - 이미지가 존재할 경우 S3 업로드 후 이미지 테이블에 저장
	 *
	 * @param requestDto 공지사항 등록 요청 DTO (제목, 내용)
	 * @param image      첨부 이미지 파일 (nullable)
	 */
	@Override
	@Transactional
	public void registerNotice(NoticeRequestDto requestDto, MultipartFile image) {
		
		// 현재 로그인한 관리자 ID
		String memberId = SecurityContextHolder
							.getContext()
							.getAuthentication()
							.getName();
		
		// 1. 공지사항 본문(Notice) 객체 생성
		Notice notice = Notice.builder()
						.noticeTitle(requestDto.getTitle())
						.noticeContent(requestDto.getContent())
						.memberId(memberId)
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
	
	/**
	 * 공지사항 상세 조회
	 * - 활성 상태(Y) 공지사항만 조회 가능
	 * - 공지사항 본문과 연결된 이미지 목록을 함께 조회
	 *
	 * @param noticeNo 공지사항 번호
	 * @return 공지사항 상세 정보 DTO
	 */
	@Override
	@Transactional(readOnly = true)
	public NoticeDetailResponseDto getNoticeDetail(Long noticeNo) {
		
		// 1. 본문 조회
		Notice notice = getActiveNoticeOrThrow(noticeNo);
		
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
	
	/**
	 * 공지사항 수정
	 * - 관리자 권한 필요
	 * - 공지사항 본문 수정
	 * - 선택된 이미지 소프트 삭제 처리
	 * - 신규 이미지가 있을 경우 S3 업로드 후 추가
	 *
	 * @param noticeNo   공지사항 번호
	 * @param requestDto 공지사항 수정 요청 DTO
	 */
	@Override
	@Transactional
	public void updateNotice(Long noticeNo, NoticeUpdateRequestDto requestDto) {
		
		// 1. 공지사항 확인
		getActiveNoticeOrThrow(noticeNo);
		
		 
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
	
	/**
	 * 공지사항 삭제 (소프트 삭제)
	 * - 관리자 권한 필요
	 * - 공지사항 상태를 비활성화 처리
	 * - 연결된 이미지도 함께 소프트 삭제
	 *
	 * @param noticeNo 공지사항 번호
	 */
	@Override
	@Transactional
	public void deleteNotice(Long noticeNo) {
		
		// 1. 공지사항 확인
		getActiveNoticeOrThrow(noticeNo);
		
		// 2. 공지사항 소프트 삭제
		noticeRepository.deleteNotice(noticeNo);
		
		// 3. 해당 공지사항 이미지 소프트 삭제 (Status 'Y' -> 'N')
		List<Long> imgIds = noticeRepository.findImgIdsByNoticeNo(noticeNo);
		if(imgIds != null && !imgIds.isEmpty()) {
			noticeRepository.deleteNoticeImages(imgIds);
		}
	}

}
