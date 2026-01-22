package com.kh.replay.universe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.global.common.ResponseData;
import com.kh.replay.universe.model.dto.UniverseCreateRequest;
import com.kh.replay.universe.model.dto.UniverseDTO;
import com.kh.replay.universe.model.dto.UniverseListResponse;
import com.kh.replay.universe.model.service.UniverseService;

import lombok.Delegate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * 
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/universes")
public class UniverseController {
    
    private final UniverseService universeService;

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
        // 검증 및 로직은 서비스가 처리함
        UniverseListResponse response = universeService.findAllUniverse(size, sort, lastUniverseId, lastLikeCount);
        
        return ResponseData.ok(response, "전체 조회 성공");
    }

    /**
     *  2. 유니버스 키워드 검색 조회
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
            @RequestParam(value = "condition", defaultValue = "all") String condition,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "latest") String sort,
            @RequestParam(value = "lastUniverseId", required = false) Long lastUniverseId,
            @RequestParam(value = "lastLikeCount", required = false) Long lastLikeCount 
    ) {
        
        UniverseListResponse response = universeService.findByKeyword(keyword, condition, size, sort, lastUniverseId, lastLikeCount);
        
        return ResponseData.ok(response, "검색 조회 성공");
    }

    /**
     * 3. 유니버스 상세 조회 
     * @param universeId
     * @return
     */
    @GetMapping("/{universeId}")
    public ResponseEntity<ResponseData<UniverseDTO>> findByUniverseId (
            @PathVariable("universeId") Long universeId
    ) {
        UniverseDTO response = universeService.findByUniverseId(universeId);
        
        return ResponseData.ok(response, "상세 조회 성공");
    }

    /**
     * 4. 유니버스 생성
     * @param request
     * @param file
     * @return
     */
    @PostMapping
    public ResponseEntity<ResponseData<Void>> createUniverse(
            @RequestPart(value = "request") UniverseCreateRequest request, 
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        
        universeService.createUniverse(request, file);
        
        // 데이터는 null, 메시지만 보냄
        return ResponseData.ok(null, "유니버스 생성 성공");
    }
    
    /**
     * 5.유니버스 수정
     * @param universeId
     * @param universe
     * @return
     */
    @PatchMapping("{universeId}")
    public ResponseEntity<ResponseData<UniverseDTO>> updateUniverse(@PathVariable("universeId")  Long universeId, @RequestBody UniverseCreateRequest universe) {
        
    	UniverseDTO resonse =  universeService.updateUniverse(universeId, universe);
        
        return ResponseData.ok(resonse, "유니버스 수정 성공");
    }
    
    @DeleteMapping("/{universeId}")
    public ResponseEntity<ResponseData<UniverseDTO>> deleteUniverse(@PathVariable("universeId") Long universeId) {
        
        // ▼▼▼ 이 로그가 안 뜨면, URL이나 Postman 설정(GET/DELETE)이 틀린 겁니다.
        log.info("============== 삭제 컨트롤러 도착! ID: {} ==============", universeId);
        
        UniverseDTO response = universeService.deleteUniverse(universeId);
        
        return ResponseData.ok(response, "유니버스 삭제 성공");
    }
    
}