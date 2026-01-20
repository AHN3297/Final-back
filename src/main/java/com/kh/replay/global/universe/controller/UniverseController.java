package com.kh.replay.global.universe.controller;

import java.util.Set;

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
    
     private static final Set<String> ALLOWED_CONDITIONS = Set.of(
        "title", "nickname", "memberId", "universeId", "hashtag", "all"
    );

    /**
     * 1. 유니버스 목록 조회 (전체)
     * @param size
     * @param sort
     * @param lastUniverseId
     * @param lastLikeCount
     * @return
     */
    @GetMapping
    public ResponseEntity<ResponseData<UniverseListResponse>> findAllUniverse(
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "lastUniverseId", required = false) Long lastUniverseId,
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount
    ) {
        // 정렬 조건 (sort)
        if (!vaildSort(sort)) {
            return ResponseData.failure("지원하지 않는 정렬 방식입니다. (latest, popular 중 선택)", HttpStatus.BAD_REQUEST);
        }
        
        //서비스 호출
        UniverseListResponse response = universeService.findAllUniverse(size, sort, lastUniverseId, lastLikeCount);
        return ResponseData.ok(response);
    }
    

    /**
     * 2. 유니버스 키워드 검색 조회
     * @param keyword
     * @param condition
     * @param size
     * @param sort
     * @param lastUniverseId
     * @param lastLikeCount
     * @return
     */
    @GetMapping("/search")
    public ResponseEntity<ResponseData<UniverseListResponse>> findByUniverse(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "condition", defaultValue = "all") String condition, // 기본값 all
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "lastUniverseId", required = false) Long lastUniverseId,
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount 
    ) {
        
        // 정렬조건 (sort)
        if (!vaildSort(sort)) {
            return ResponseData.failure("지원하지 않는 정렬 방식입니다. (latest, popular 중 선택)", HttpStatus.BAD_REQUEST);
        }

        // 검색 조건 (condition)
        if (!ALLOWED_CONDITIONS.contains(condition)) {
            return ResponseData.failure("지원하지 않는 검색 조건입니다. (title, nickname, memberId, universeId, hashtag, all)", HttpStatus.BAD_REQUEST);
        }

        // 키워드 길이 (최소 2글자 이상)
        if (keyword == null || keyword.trim().length() < 2) {
             return ResponseData.failure("검색어는 최소 2글자 이상이어야 합니다.", HttpStatus.BAD_REQUEST);
        }
        
        // 서비스 호출
        UniverseListResponse response = universeService.findByKeyword(keyword, condition, size, sort, lastUniverseId, lastLikeCount);
        
        //검색결과 대기
        
        return ResponseData.ok(response);
    }


    
    // 정렬 조건 유효성 검사
    private boolean vaildSort(String sort) {
        return "latest".equals(sort) || "popular".equals(sort);
    }
}