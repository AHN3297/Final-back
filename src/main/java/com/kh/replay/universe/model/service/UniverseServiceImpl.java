package com.kh.replay.universe.model.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.s3.S3Service;
import com.kh.replay.universe.model.dao.UniverseMapper;
import com.kh.replay.universe.model.dto.UniverseCreateRequest;
import com.kh.replay.universe.model.dto.UniverseDTO;
import com.kh.replay.universe.model.dto.UniverseListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UniverseServiceImpl implements UniverseService {

	private final UniverseMapper universeMapper;
	private final S3Service s3Service;
	private final UniverseValidator validator;

	private static final Set<String> ALLOWED_CONDITIONS = Set.of(
		"title", "nickname", "memberId", "universeId", "hashtag", "all"
	);

	@Override
	@Transactional(readOnly = true)
	public UniverseListResponse findAllUniverse(int size, String sort, Long lastUniverseId, Long lastLikeCount) {
		validator.validateSort(sort);

		Map<String, Object> universeSearch = new HashMap<>();
		universeSearch.put("sort", sort);
		universeSearch.put("lastUniverseId", lastUniverseId);
		universeSearch.put("lastLikeCount", lastLikeCount);
		universeSearch.put("limit", size + 1);

		List<UniverseDTO> list = universeMapper.findAllUniverse(universeSearch);
		return buildResponse(list, size, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public UniverseListResponse findByKeyword(String keyword, String condition, int size, String sort,
			Long lastUniverseId, Long lastLikeCount) {
		validator.validateSort(sort);

		if (!ALLOWED_CONDITIONS.contains(condition)) {
			throw new IllegalArgumentException("지원하지 않는 검색 조건입니다.");
		}

		if (keyword == null || keyword.trim().length() < 2) {
			throw new IllegalArgumentException("검색어는 최소 2글자 이상이어야 합니다.");
		}

		Map<String, Object> universeSearch = new HashMap<>();
		universeSearch.put("keyword", keyword);
		universeSearch.put("condition", condition);
		universeSearch.put("sort", sort);
		universeSearch.put("lastUniverseId", lastUniverseId);
		universeSearch.put("lastLikeCount", lastLikeCount);
		universeSearch.put("limit", size + 1);

		List<UniverseDTO> list = universeMapper.findByKeyword(universeSearch);
		return buildResponse(list, size, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public UniverseDTO findByUniverseId(Long universeId) {
		return validator.validateExisting(universeId);
	}

	@Override
	public void createUniverse(UniverseCreateRequest request, MultipartFile file, String userId) {
		String imageUrl = null;

		if (file != null && !file.isEmpty()) {
			try {
				imageUrl = s3Service.uploadFile(file);
			} catch (IOException e) {
				throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
			}
		}

		UniverseDTO universe = UniverseDTO.builder()
				.title(request.getTitle())
				.layoutData(request.getLayoutData())
				.themeCode(request.getThemeCode())
				.memberId(userId)
				.thumbnailUrl(imageUrl)
				.build();

		int result = universeMapper.insertUniverse(universe);

		if (result == 0) {
			throw new RuntimeException("유니버스 생성에 실패했습니다.");
		}
	}

	@Override
	public UniverseDTO updateUniverse(Long universeId, UniverseCreateRequest request, String userId) {
		UniverseDTO existing = validator.validateExisting(universeId);
		validator.validateOwner(existing, userId);

		UniverseDTO update = UniverseDTO.builder()
				.universeId(universeId)
				.title(request.getTitle())
				.layoutData(request.getLayoutData())
				.themeCode(request.getThemeCode())
				.status(request.getStatus())
				.build();

		universeMapper.updateUniverse(update);

		return validator.validateExisting(universeId);
	}

	@Override
	public UniverseDTO deleteUniverse(Long universeId, String userId) {
		UniverseDTO existing = validator.validateExisting(universeId);
		validator.validateOwner(existing, userId);

		universeMapper.deleteUniverse(universeId);

		try {
			if (existing.getThumbnailUrl() != null) {
				s3Service.deleteFile(existing.getThumbnailUrl());
			}
		} catch (Exception e) {
			throw new RuntimeException("S3 파일 삭제 중 오류 발생", e);
		}

		return existing;
	}

	@Override
	@Transactional(readOnly = true)
	public UniverseListResponse findBookmarkedUniverses(String memberId, int size, Long lastUniverseId) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("lastUniverseId", lastUniverseId);
		params.put("limit", size + 1);

		List<UniverseDTO> list = universeMapper.findBookmarkedUniverses(params);
		return buildSimpleResponse(list, size);
	}

	@Override
	@Transactional(readOnly = true)
	public UniverseListResponse findLikedUniverses(String memberId, int size, Long lastUniverseId) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("lastUniverseId", lastUniverseId);
		params.put("limit", size + 1);

		List<UniverseDTO> list = universeMapper.findLikedUniverses(params);
		return buildSimpleResponse(list, size);
	}

	private UniverseListResponse buildResponse(List<UniverseDTO> list, int size, String sort) {
		boolean hasNext = false;
		if (list.size() > size) {
			hasNext = true;
			list.remove(size);
		}

		Long nextLastUniverseId = null;
		Long nextLastLikeCount = null;

		if (!list.isEmpty()) {
			UniverseDTO lastItem = list.get(list.size() - 1);
			nextLastUniverseId = lastItem.getUniverseId();

			if ("popular".equals(sort)) {
				nextLastLikeCount = lastItem.getLike();
			}
		}

		UniverseListResponse.Pagination pagination = UniverseListResponse.Pagination.builder()
				.hasNext(hasNext)
				.lastUniverseId(nextLastUniverseId)
				.lastLikeCount(nextLastLikeCount)
				.size(size)
				.build();

		return UniverseListResponse.builder()
				.content(list)
				.pagination(pagination)
				.build();
	}

	private UniverseListResponse buildSimpleResponse(List<UniverseDTO> list, int size) {
		boolean hasNext = false;
		if (list.size() > size) {
			hasNext = true;
			list.remove(size);
		}

		Long nextLastUniverseId = null;
		if (!list.isEmpty()) {
			nextLastUniverseId = list.get(list.size() - 1).getUniverseId();
		}

		UniverseListResponse.Pagination pagination = UniverseListResponse.Pagination.builder()
				.hasNext(hasNext)
				.lastUniverseId(nextLastUniverseId)
				.size(size)
				.build();

		return UniverseListResponse.builder()
				.content(list)
				.pagination(pagination)
				.build();
	}
}