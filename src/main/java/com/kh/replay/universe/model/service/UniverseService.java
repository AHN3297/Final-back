package com.kh.replay.universe.model.service;

import org.springframework.web.multipart.MultipartFile;

import com.kh.replay.universe.model.dto.UniverseCreateRequest;
import com.kh.replay.universe.model.dto.UniverseDTO;
import com.kh.replay.universe.model.dto.UniverseListResponse;

public interface UniverseService {
    
	//전체조회
	UniverseListResponse findAllUniverse(int size, String sort, Long lastUniverseId, Long lastLikeCount);
	
	//키워드조회
	UniverseListResponse findByKeyword(String keyword, String condition, int size, String sort, Long lastUniverseId, Long lastLikeCount);
	
	//상세조회
	UniverseDTO findByUniverseId(Long universeId);
	
	//유니버스 생성
	void createUniverse(UniverseCreateRequest request, MultipartFile file); 
}