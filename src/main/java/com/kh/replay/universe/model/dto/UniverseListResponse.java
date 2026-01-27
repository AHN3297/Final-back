package com.kh.replay.universe.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniverseListResponse {

    private List<UniverseDTO> content;
    private Pagination pagination;

    @Getter
    @Builder
    public static class Pagination {
        private boolean hasNext;
        private Long lastUniverseId;
        private Long lastLikeCount;
        private int size;
    }

}