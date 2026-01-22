package com.kh.replay.universe.model.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.exception.ForbiddenException; 
import com.kh.replay.global.exception.ResourceNotFoundException;
import com.kh.replay.global.s3.S3Service;
import com.kh.replay.universe.model.dto.UniverseCreateRequest;
import com.kh.replay.universe.model.dto.UniverseDTO;
import com.kh.replay.universe.model.dto.UniverseListResponse;
import com.kh.replay.universe.model.mapper.UniverseMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UniverseServiceImpl implements UniverseService {

    private final UniverseMapper universeMapper;
    private final S3Service s3Service;

    private static final Set<String> ALLOWED_CONDITIONS = Set.of(
        "title", "nickname", "memberId", "universeId", "hashtag", "all"
    );

    /**
     * 1. 전체 목록 조회
     */
    @Override
    public UniverseListResponse findAllUniverse(int size, String sort, Long lastUniverseId, Long lastLikeCount) {
        
        // 1) 정렬 조건 검증
        if (!isValidSort(sort)) {
            throw new IllegalArgumentException("지원하지 않는 정렬 방식입니다. (latest, popular 중 선택)");
        }

        int limit = size + 1;
        List<UniverseDTO> list = universeMapper.findAllUniverse(sort, lastUniverseId, lastLikeCount, limit);
        
        return buildResponse(list, size, sort);
    }

    /**
     * 2. 키워드 검색 조회
     */
    @Override
    public UniverseListResponse findByKeyword(String keyword, String condition, int size, String sort,
            Long lastUniverseId, Long lastLikeCount) {
        
        // 1) 정렬 조건 검증
        if (!isValidSort(sort)) {
            throw new IllegalArgumentException("지원하지 않는 정렬 방식입니다. (latest, popular 중 선택)");
        }
        
        // 2) 검색 조건 검증
        if (!ALLOWED_CONDITIONS.contains(condition)) {
             throw new IllegalArgumentException("지원하지 않는 검색 조건입니다.");
        }

        // 3) 키워드 길이 검증
        if (keyword == null || keyword.trim().length() < 2) {
             throw new IllegalArgumentException("검색어는 최소 2글자 이상이어야 합니다.");
        }

        int limit = size + 1;
        List<UniverseDTO> list = universeMapper.findByKeyword(keyword, condition, sort, lastUniverseId, lastLikeCount, limit);
        
        return buildResponse(list, size, sort);
    }
    
    /**
     * 3. 상세조회
     */
    @Override
    public UniverseDTO findByUniverseId(Long universeId) {
    	
        UniverseDTO universe = universeMapper.findByUniverseId(universeId);
 
        return universe;
    }

    /**
     * 4. 생성 
     */
    @Override
    @Transactional
    public void createUniverse(UniverseCreateRequest request, MultipartFile file) { 
        
        String imageUrl = null;
        
        // S3 업로드
        if (file != null && !file.isEmpty()) {
            try {
                imageUrl = s3Service.uploadFile(file);
            } catch (IOException e) {
                log.error("S3 Upload Failed", e);
                throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.");
            }
        }
        
        // DTO 생성
        UniverseDTO universe = UniverseDTO.builder()
                .title(request.getTitle())
                .layoutData(request.getLayoutData())
                .themeCode(request.getThemeCode())
                .memberId(request.getMemberId())
                .thumbnailUrl(imageUrl)
                .build();
        
        // DB 저장
        int result = universeMapper.insertUniverse(universe);
        
        // 예외처리
        if (result == 0) {
            throw new RuntimeException("유니버스 생성에 실패했습니다.");
        }
    }

    /**
     * 5. 수정 (PATCH)
     */
    @Override
    @Transactional
    public UniverseDTO updateUniverse(Long universeId, UniverseCreateRequest request) { // 변수명 request로 통일
        
        // 400 (필수값 누락)
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("유니버스 제목은 필수 입력 값입니다.");
        }

        // 404 (데이터 없음)
        UniverseDTO existing = universeMapper.findByUniverseId(universeId);
        if (existing == null) {  
            throw new ResourceNotFoundException("해당 유니버스를 찾을 수 없습니다."); 
        }

        // 403 (권한 없음)
        if (!existing.getMemberId().equals(request.getMemberId())) {
            throw new ForbiddenException("해당 유니버스 접근 권한이 없습니다."); 
        }
        
        UniverseDTO update = UniverseDTO.builder()
                .universeId(universeId)
                .title(request.getTitle())
                .layoutData(request.getLayoutData())
                .themeCode(request.getThemeCode()) 
                .status(request.getStatus())
                .build();
        
        universeMapper.updateUniverse(update);
        
        return universeMapper.findByUniverseId(universeId);
    }
    
    
    /**
     *6. 삭제 (DELETE)
     */
    @Override
    @Transactional
    public UniverseDTO deleteUniverse(Long universeId) {
        
        // 1. 조회
        UniverseDTO existing = universeMapper.findByUniverseId(universeId);
        
        // 2. 데이터 없으면 예외 발생
        if (existing == null) {  
            log.info(">>> 데이터가 없습니다. ResourceNotFoundException 발생!");
            throw new ResourceNotFoundException("해당 유니버스를 찾을 수 없습니다."); 
        }

        // 3. 삭제 진행
        universeMapper.deleteUniverse(universeId);
        log.info(">>> 삭제 완료. ID: {}", universeId);
        
        return existing;
    }

    
    // --- 내부 메서드 ---

    // 정렬 조건 유효성 검사
    private boolean isValidSort(String sort) {
        return "latest".equals(sort) || "popular".equals(sort);
    }
    
    
    // 조회 검사 
    private UniverseDTO searchExisting(Long universeId) {
    	
    	UniverseDTO existing = universeMapper.findByUniverseId(universeId);
    	
        if (existing == null) {  
            throw new ResourceNotFoundException("해당 유니버스를 찾을 수 없습니다."); 
        }
    	return existing;
    } 
    
  
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