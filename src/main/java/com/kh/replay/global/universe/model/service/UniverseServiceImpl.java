package com.kh.replay.global.universe.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.universe.model.dto.UniverseDTO;
import com.kh.replay.global.universe.model.dto.UniverseListResponse;
import com.kh.replay.global.universe.model.mapper.UniverseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UniverseServiceImpl implements UniverseService {

    private final UniverseMapper universeMapper;

    /**
     * 1. 전체 목록 조회
     */
    @Override
    public UniverseListResponse findAllUniverse(int size, String sort, Long lastUniverseId, Long lastLikeCount) {
        int limit = size + 1;
        // Mapper 호출 (전체 조회)
        List<UniverseDTO> list = universeMapper.findAllUniverse(sort, lastUniverseId, lastLikeCount, limit);
        
        return buildResponse(list, size, sort);
    }

    /**
     * 2. 키워드 검색 조회
     */
    @Override
    public UniverseListResponse findByKeyword(String keyword, String condition, int size, String sort,
            Long lastUniverseId, Long lastLikeCount) {
        int limit = size + 1;
        // Mapper 호출 (검색 조회) -> Mapper에 이 메서드 추가 필요!
        List<UniverseDTO> list = universeMapper.findByKeyword(keyword, condition, sort, lastUniverseId, lastLikeCount, limit);
        
        return buildResponse(list, size, sort);
    }

    
    // 공통로직
    private UniverseListResponse buildResponse(List<UniverseDTO> list, int size, String sort) {
        
        // 1. hasNext 확인 및 데이터 자르기
        boolean hasNext = false;
        if (list.size() > size) {
            hasNext = true;
            list.remove(size); // limit(+1)로 가져온 마지막 데이터 제거
        }

        // 2. 다음 커서(Next Cursor) 계산
        Long nextLastUniverseId = null;
        Long nextLastLikeCount = null;
        
        if (!list.isEmpty()) {
            UniverseDTO lastItem = list.get(list.size() - 1);
            nextLastUniverseId = lastItem.getUniverseId();
            
            // 인기순일 경우 좋아요 수도 커서로 사용
            if ("popular".equals(sort)) {
                nextLastLikeCount = lastItem.getLike();
            }
        }

        // 3. 응답 DTO 조립
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