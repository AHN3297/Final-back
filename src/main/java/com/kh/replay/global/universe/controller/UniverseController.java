package com.kh.replay.global.universe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.global.common.ResponseData;
import com.kh.replay.global.universe.model.dto.UniverseListResponse;
import com.kh.replay.global.universe.model.service.UniverseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/universes") 
public class UniverseController {
    
    private final UniverseService universeService;
    
    /**
     * 유니버스 목록 조회 (무한스크롤 기반)
     * GET /api/universes?size=10&sort=latest&lastUniverseId=...
     * @param int size
     * @param String sort
     * @param Long lastUniverseId
     * @param Long lastLikeCount
     */
    
    @GetMapping
    public ResponseEntity<ResponseData<UniverseListResponse>> findAllUniverse(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "lastUniverseId", required = false) Long lastUniverseId,
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount
    ) {
    	
    	if (!"latest".equals(sort) && !"popular".equals(sort)) {
            return ResponseData.failure("지원하지 않는 정렬 방식입니다. (latest, popular 중 선택)", HttpStatus.BAD_REQUEST);
        }
        
        UniverseListResponse response = universeService.findAllUniverse(size, sort, lastUniverseId, lastLikeCount);
        
        return ResponseData.ok(response);
    }
}