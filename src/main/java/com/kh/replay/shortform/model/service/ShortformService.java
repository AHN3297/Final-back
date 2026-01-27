package com.kh.replay.shortform.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.shortform.model.dto.ShortformCreateRequest;
import com.kh.replay.shortform.model.dto.ShortformDTO;
import com.kh.replay.shortform.model.dto.ShortformListResponse;

public interface ShortformService {

	// 전체 목록 조회
	ShortformListResponse findAllShortform(int size, String sort, Long lastShortFormId, Long lastLikeCount);

	// 키워드 검색 조회
	ShortformListResponse findByKeyword(String keyword, String condition, int size, String sort,
			Long lastShortFormId, Long lastLikeCount);

	// 상세 조회
	ShortformDTO findByShortFormId(Long shortFormId);

	// 생성
	void createShortform(ShortformCreateRequest request, MultipartFile video, MultipartFile thumbnail, String userId);

	// 수정
	ShortformDTO updateShortform(Long shortFormId, ShortformCreateRequest request, String userId);

	// 삭제
	ShortformDTO deleteShortform(Long shortFormId, String userId);

	// 좋아요한 숏폼 목록 조회
	ShortformListResponse findLikedShortforms(String memberId, int size, Long lastShortFormId);

	// 내가 작성한 숏폼 목록 조회
	ShortformListResponse findMyShortforms(String memberId, int size, Long lastShortFormId);

}
