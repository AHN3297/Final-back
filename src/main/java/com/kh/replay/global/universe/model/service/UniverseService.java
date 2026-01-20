package com.kh.replay.global.universe.model.service;

import java.util.List;
import com.kh.replay.global.universe.model.dto.UniverseDTO;
import com.kh.replay.global.universe.model.dto.UniverseListResponse;

public interface UniverseService {
    
	//전체조회
	UniverseListResponse findAllUniverse(int size, String sort, Long lastUniverseId, Long lastLikeCount);
	
	//키워드조회
	UniverseListResponse findByKeyword(String keyword, String condition, int size, String sort, Long lastUniverseId, Long lastLikeCount); 
}