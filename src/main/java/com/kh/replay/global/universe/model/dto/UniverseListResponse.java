package com.kh.replay.global.universe.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniverseListResponse {
    
    // 1. 실제 유니버스 데이터 목록
    private List<UniverseDTO> content; 
    
    // 2. 무한 스크롤을 위한 페이징 정보 
    private Pagination pagination;     

    @Data
    @Builder
    public static class Pagination {
        private boolean hasNext; // 다음 페이지 존재 여부
        private Long lastUniverseId; // 마지막 유니버스 ID (커서)
        private Long lastLikeCount; // 마지막 좋아요 수 (인기순 정렬용 커서)
        private int size;// 요청 사이즈
    }
}