package com.kh.replay.shortform.model.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.s3.S3Service;
import com.kh.replay.shortform.model.dao.ShortformMapper;
import com.kh.replay.shortform.model.dto.ShortformCreateRequest;
import com.kh.replay.shortform.model.dto.ShortformDTO;
import com.kh.replay.shortform.model.dto.ShortformListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShortformServiceImpl implements ShortformService {

	private final ShortformMapper shortformMapper;
	private final S3Service s3Service;
	private final ShortformValidator validator;

	private static final Set<String> ALLOWED_CONDITIONS = Set.of(
		"title", "nickname", "caption", "all"
	);

	@Override
	@Transactional(readOnly = true)
	public ShortformListResponse findAllShortform(int size, String sort, Long lastShortFormId, Long lastLikeCount) {
		validator.validateSort(sort);

		Map<String, Object> shortformSearch = new HashMap<>();
		shortformSearch.put("sort", sort);
		shortformSearch.put("lastShortFormId", lastShortFormId);
		shortformSearch.put("lastLikeCount", lastLikeCount);
		shortformSearch.put("limit", size + 1);

		List<ShortformDTO> list = shortformMapper.findAllShortform(shortformSearch);
		return buildResponse(list, size, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public ShortformListResponse findByKeyword(String keyword, String condition, int size, String sort,
			Long lastShortFormId, Long lastLikeCount) {
		validator.validateSort(sort);

		if (!ALLOWED_CONDITIONS.contains(condition)) {
			throw new IllegalArgumentException("지원하지 않는 검색 조건입니다.");
		}
		if (keyword == null || keyword.trim().length() < 2) {
			throw new IllegalArgumentException("검색어는 최소 2글자 이상이어야 합니다.");
		}

		Map<String, Object> shortformSearch = new HashMap<>();
		shortformSearch.put("keyword", keyword);
		shortformSearch.put("condition", condition);
		shortformSearch.put("sort", sort);
		shortformSearch.put("lastShortFormId", lastShortFormId);
		shortformSearch.put("lastLikeCount", lastLikeCount);
		shortformSearch.put("limit", size + 1);

		List<ShortformDTO> list = shortformMapper.findByKeyword(shortformSearch);
		return buildResponse(list, size, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public ShortformDTO findByShortFormId(Long shortFormId) {
		return validator.validateExisting(shortFormId);
	}

	@Override
	public void createShortform(ShortformCreateRequest request, MultipartFile video, MultipartFile thumbnail, String userId) {
		// [수정] 파일 존재 여부 검사는 Controller로 이동했거나, 여기서 유지하되 예외 전파 방식 변경
		
		String videoUrl = null;
		String thumbnailUrl = null;
		Long duration = 0L; 

		// [수정] try-catch 제거 또는 S3Service가 런타임 예외를 던진다고 가정
		// 만약 S3Service가 Checked Exception(IOException)을 던진다면 
		// "글로벌 핸들러로 예외 던져주세요" 피드백에 따라 여기서 잡아서 로그만 찍는 행위 금지 -> 런타임 예외로 감싸서 던짐
		try {
			videoUrl = s3Service.uploadFile(video);
		} catch (IOException e) {
			throw new RuntimeException("동영상 업로드 실패", e); // 예외를 먹지 않고 던짐
		}

		try {
			thumbnailUrl = s3Service.uploadFile(thumbnail);
		} catch (IOException e) {
			// 썸네일 실패 시 앞서 올린 비디오 삭제 시도 (이것도 예외 전파)
			try {
				s3Service.deleteFile(videoUrl);
			} catch (Exception deleteEx) {
				// 삭제 실패는 로그만 남길 수 밖에 없음 (주 흐름 방해 X)
			}
			throw new RuntimeException("썸네일 업로드 실패", e);
		}

		ShortformDTO shortform = ShortformDTO.builder()
				.shortFormTitle(request.getShortFormTitle())
				.videoUrl(videoUrl)
				.thumbnailUrl(thumbnailUrl)
				.caption(request.getCaption())
				.duration(duration) 
				.memberId(userId)
				.build();

		int result = shortformMapper.insertShortform(shortform);

		if (result == 0) {
			throw new RuntimeException("숏폼 생성에 실패했습니다.");
		}
	}

	@Override
	public ShortformDTO updateShortform(Long shortFormId, ShortformCreateRequest request, String userId) {
		ShortformDTO existing = validator.validateExisting(shortFormId);
		validator.validateOwner(existing, userId);

		// [수정] 제목 null 체크 로직 삭제 (Controller @Valid가 처리함)

		ShortformDTO update = ShortformDTO.builder()
				.shortFormId(shortFormId)
				.shortFormTitle(request.getShortFormTitle())
				.caption(request.getCaption())
				.status(request.getStatus())
				.build();

		shortformMapper.updateShortform(update);

		return validator.validateExisting(shortFormId);
	}

	@Override
	public ShortformDTO deleteShortform(Long shortFormId, String userId) {
		ShortformDTO existing = validator.validateExisting(shortFormId);
		validator.validateOwner(existing, userId);

		shortformMapper.deleteShortform(shortFormId);

		// [수정] deleteS3FileSafely 대신 예외를 던지는 로직으로 변경
		// "글로벌 핸들러로 예외 던져주세요" -> 즉, 여기서 try-catch로 예외를 숨기지 말라는 뜻
		try {
			if (existing.getVideoUrl() != null) s3Service.deleteFile(existing.getVideoUrl());
			if (existing.getThumbnailUrl() != null) s3Service.deleteFile(existing.getThumbnailUrl());
		} catch (Exception e) {
			// 피드백 반영: 로그만 찍고 넘어가는 것 금지 -> 예외 발생 시킴
			throw new RuntimeException("S3 파일 삭제 중 오류 발생", e);
		}

		return existing;
	}

	private ShortformListResponse buildResponse(List<ShortformDTO> list, int size, String sort) {
		boolean hasNext = false;
		if (list.size() > size) {
			hasNext = true;
			list.remove(size);
		}

		Long nextLastShortFormId = null;
		Long nextLastLikeCount = null;

		if (!list.isEmpty()) {
			ShortformDTO lastItem = list.get(list.size() - 1);
			nextLastShortFormId = lastItem.getShortFormId();

			if ("popular".equals(sort)) {
				nextLastLikeCount = lastItem.getLike();
			}
		}

		ShortformListResponse.Pagination pagination = ShortformListResponse.Pagination.builder()
				.hasNext(hasNext)
				.lastShortFormId(nextLastShortFormId)
				.lastLikeCount(nextLastLikeCount)
				.size(size)
				.build();

		return ShortformListResponse.builder()
				.content(list)
				.pagination(pagination)
				.build();
	}
}