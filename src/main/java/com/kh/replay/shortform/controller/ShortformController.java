package com.kh.replay.shortform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.common.ResponseData;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.global.like.model.service.LikeService;
import com.kh.replay.global.report.model.dto.ReportRequestDTO;
import com.kh.replay.global.report.model.dto.ReportResponse;
import com.kh.replay.global.report.model.service.ReportService;
import com.kh.replay.member.model.vo.CustomUserDetails;
import com.kh.replay.shortform.model.dto.ShortformCreateRequest;
import com.kh.replay.shortform.model.dto.ShortformDTO;
import com.kh.replay.shortform.model.dto.ShortformListResponse;
import com.kh.replay.shortform.model.service.ShortformService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shortforms")
public class ShortformController {

	private final ShortformService shortformService;
	private final LikeService likeService;
	private final ReportService reportService;

	/**
	 * 1. 숏폼 목록 조회 (전체)
	 */
	@GetMapping
	public ResponseEntity<ResponseData<ShortformListResponse>> findAllShortform(
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sort", defaultValue = "latest") String sort,
			@RequestParam(value = "lastShortFormId", required = false) Long lastShortFormId,
			@RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount
	) {
		ShortformListResponse response = shortformService.findAllShortform(size, sort, lastShortFormId, lastLikeCount);
		return ResponseData.ok(response, "전체 조회 성공");
	}

	/**
	 * 2. 숏폼 키워드 검색 조회
	 */
	@GetMapping("/search")
	public ResponseEntity<ResponseData<ShortformListResponse>> findByKeyword(
			@RequestParam(value = "keyword") String keyword,
			@RequestParam(value = "condition", defaultValue = "all") String condition,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sort", defaultValue = "latest") String sort,
			@RequestParam(value = "lastShortFormId", required = false) Long lastShortFormId,
			@RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount
	) {
		ShortformListResponse response = shortformService.findByKeyword(keyword, condition, size, sort, lastShortFormId, lastLikeCount);
		return ResponseData.ok(response, "검색 조회 성공");
	}

	/**
	 * 3. 숏폼 상세 조회
	 */
	@GetMapping("/{shortFormId}")
	public ResponseEntity<ResponseData<ShortformDTO>> findByShortFormId(
			@PathVariable("shortFormId") Long shortFormId
	) {
		ShortformDTO response = shortformService.findByShortFormId(shortFormId);
		return ResponseData.ok(response, "상세 조회 성공");
	}

	/**
	 * 4. 숏폼 생성
	 */
	@PostMapping
	public ResponseEntity<ResponseData<Void>> createShortform(
			@RequestPart(value = "request") ShortformCreateRequest request,
			@RequestPart(value = "video") MultipartFile video,
			@RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		shortformService.createShortform(request, video, thumbnail, user.getUsername());
		return ResponseData.ok(null, "숏폼 생성 성공");
	}

	/**
	 * 5. 숏폼 수정
	 */
	@PatchMapping("/{shortFormId}")
	public ResponseEntity<ResponseData<ShortformDTO>> updateShortform(
			@PathVariable("shortFormId") Long shortFormId,
			@RequestBody ShortformCreateRequest request,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		ShortformDTO response = shortformService.updateShortform(shortFormId, request, user.getUsername());
		return ResponseData.ok(response, "숏폼 수정 성공");
	}

	/**
	 * 6. 숏폼 삭제
	 */
	@DeleteMapping("/{shortFormId}")
	public ResponseEntity<ResponseData<ShortformDTO>> deleteShortform(
			@PathVariable("shortFormId") Long shortFormId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		ShortformDTO response = shortformService.deleteShortform(shortFormId, user.getUsername());
		return ResponseData.ok(response, "숏폼 삭제 성공");
	}

	/**
	 * 7. 숏폼 좋아요 (생성)
	 */
	@PostMapping("/{shortFormId}/like")
	public ResponseEntity<ResponseData<LikeResponse>> likeShortform(
			@PathVariable("shortFormId") Long shortFormId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		LikeResponse response = likeService.likeShortform(shortFormId, user.getUsername());
		return ResponseData.ok(response, "좋아요를 눌렀습니다.");
	}

	/**
	 * 8. 숏폼 좋아요 취소 (삭제)
	 */
	@DeleteMapping("/{shortFormId}/like")
	public ResponseEntity<ResponseData<LikeResponse>> unlikeShortform(
			@PathVariable("shortFormId") Long shortFormId,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		LikeResponse response = likeService.unlikeShortform(shortFormId, user.getUsername());
		return ResponseData.ok(response, "좋아요를 취소했습니다.");
	}

	/**
	 * 9. 숏폼 신고
	 */
	@PostMapping("/{shortFormId}/report")
	public ResponseEntity<ResponseData<ReportResponse>> reportShortform(
			@PathVariable("shortFormId") Long shortFormId,
			@RequestBody ReportRequestDTO request,
			@AuthenticationPrincipal CustomUserDetails user
	) {
		ReportResponse response = reportService.createReport("SHORTFORM", shortFormId, user.getUsername(), request);
		return ResponseData.ok(response, "신고가 접수되었습니다.");
	}

}
