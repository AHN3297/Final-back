package com.kh.replay.global.universe.model.service;

import java.util.List;
import com.kh.replay.global.universe.model.dto.UniverseDTO;
import com.kh.replay.global.universe.model.dto.UniverseListResponse;

public interface UniverseService {
    

	UniverseListResponse findAllUniverse(int size, String sort, Long lastUniverseId, Long lastLikeCount); 
}