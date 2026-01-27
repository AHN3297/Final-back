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

		String videoUrl = null;
		String thumbnailUrl = null;
		Long duration = 0L; 

		try {
			videoUrl = s3Service.uploadFile(video);
		} catch (IOException e) {
			throw new RuntimeException("동영상 업로드 실패", e); // 예외를 먹지 않고 던짐
		}

		try {
			thumbnailUrl = s3Service.uploadFile(thumbnail);
		} catch (IOException e) {
			try {
				s3Service.deleteFile(videoUrl);
			} catch (Exception deleteEx) {
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

		try {
			if (existing.getVideoUrl() != null) s3Service.deleteFile(existing.getVideoUrl());
			if (existing.getThumbnailUrl() != null) s3Service.deleteFile(existing.getThumbnailUrl());
		} catch (Exception e) {
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
