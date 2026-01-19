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
public class UniverseServiceImpl implements UniverseService { // 인터페이스 있다면 implements 유지

    private final UniverseMapper universeMapper;

    public UniverseListResponse findAllUniverse(int size, String sort, Long lastUniverseId, Long lastLikeCount) {
        
    	//추후 리펙토링으로 따로 뺼 예정 
    	
        int limit = size + 1;
        List<UniverseDTO> list = universeMapper.findAllUniverse(sort, lastUniverseId, lastLikeCount, limit);

        // 2. hasNext 확인 및 데이터 자르기
        boolean hasNext = false;
        if (list.size() > size) {
            hasNext = true;
            list.remove(size); // 마지막 1개(확인용) 제거
        }

        // 3. 다음 커서(Next Cursor) 계산
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

        // 4. 응답 DTO 조립
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