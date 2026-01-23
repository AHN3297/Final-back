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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UniverseServiceImpl implements UniverseService {

    private final UniverseMapper universeMapper;
    private final S3Service s3Service;
    private final UniverseValidator validator; 

    // 검색 허용 조건
    private static final Set<String> ALLOWED_CONDITIONS = Set.of(
        "title", "nickname", "memberId", "universeId", "hashtag", "all"
    );

    /**
     * 1. 전체 목록 조회 (Map 파라미터 적용)
     */
    @Override
    @Transactional(readOnly = true)
    public UniverseListResponse findAllUniverse(int size, String sort, Long lastUniverseId, Long lastLikeCount) {
        
        // 검색조건 검증
        validator.validateSort(sort); 

        int limit = size + 1;
        
        // Mapper로 넘길 파라미터 Map 생성
        Map<String, Object> params = new HashMap<>();
        params.put("sort", sort);
        params.put("lastUniverseId", lastUniverseId);
        params.put("lastLikeCount", lastLikeCount);
        params.put("limit", limit);

        List<UniverseDTO> list = universeMapper.findAllUniverse(params);
        
        return buildResponse(list, size, sort);
    }

    /**
     * 2. 키워드 검색 조회 (Map 파라미터 적용)
     */
    @Override
    @Transactional(readOnly = true)
    public UniverseListResponse findByKeyword(String keyword, String condition, int size, String sort,
            Long lastUniverseId, Long lastLikeCount) {
        
        // 검색조건 검증
        validator.validateSort(sort);
        
        if (!ALLOWED_CONDITIONS.contains(condition)) {
             throw new IllegalArgumentException("지원하지 않는 검색 조건입니다.");
        }

        if (keyword == null || keyword.trim().length() < 2) {
             throw new IllegalArgumentException("검색어는 최소 2글자 이상이어야 합니다.");
        }

        int limit = size + 1;

        // Mapper로 넘길 파라미터 Map 생성
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", keyword);
        params.put("condition", condition);
        params.put("sort", sort);
        params.put("lastUniverseId", lastUniverseId);
        params.put("lastLikeCount", lastLikeCount);
        params.put("limit", limit);

        List<UniverseDTO> list = universeMapper.findByKeyword(params);
        
        return buildResponse(list, size, sort);
    }
    
    /**
     * 3. 상세조회 (기존 유지)
     */
    @Override
    @Transactional(readOnly = true)
    public UniverseDTO findByUniverseId(Long universeId) {
        
        // 존재 확인
        return validator.validateExisting(universeId);
    }

    /**
     * 4. 생성 (기존 유지)
     */
    @Override
    public void createUniverse(UniverseCreateRequest request, MultipartFile file, String userId) { 
        
        String imageUrl = null;
        
        // S3 이미지 업로드
        if (file != null && !file.isEmpty()) {
            try {
                imageUrl = s3Service.uploadFile(file);
            } catch (IOException e) {
                log.error("S3 Upload Failed", e);
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.");
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


    /**
     * 5. 수정 (PATCH) (기존 유지)
     */
    @Override
    public UniverseDTO updateUniverse(Long universeId, UniverseCreateRequest request, String userId) {
        
        // 1. 존재 확인 
        UniverseDTO existing = validator.validateExisting(universeId);
        
        // 2. 권한 확인 
        validator.validateOwner(existing, userId);
        
        // 3. 필수값 확인
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("유니버스 제목은 필수 입력 값입니다.");
        }

        // 4. 수정
        UniverseDTO update = UniverseDTO.builder()
                .universeId(universeId)
                .title(request.getTitle())
                .layoutData(request.getLayoutData())
                .themeCode(request.getThemeCode()) 
                .status(request.getStatus())
                .build();
        
        universeMapper.updateUniverse(update);
        
        // 5. 수정된 결과 반환
        return validator.validateExisting(universeId);
    }
    
    /**
     * 6. 삭제 (DELETE) (기존 유지)
     */
    @Override
    public UniverseDTO deleteUniverse(Long universeId, String userId) {
        
        // 1. 존재 확인
        UniverseDTO existing = validator.validateExisting(universeId);
        
        // 2. 권한 확인 
        validator.validateOwner(existing, userId);

        // 3. 삭제 진행
        universeMapper.deleteUniverse(universeId);
        log.info(">>> 유니버스 삭제 완료. ID: {}", universeId);
        
        return existing;
    }

    
    // --- 내부 유틸 메서드 ---

    // 무한스크롤 응답 빌더
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
}