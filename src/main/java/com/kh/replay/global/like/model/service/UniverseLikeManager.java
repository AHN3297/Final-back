package com.kh.replay.global.like.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kh.replay.global.like.model.dao.LikeMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UniverseLikeManager {

    private final LikeMapper likeMapper;
    
    public void createLike(Long universeId, String memberId) {
        // Mapper에 전달할 Map 생성
        Map<String, Object> params = new HashMap<>();
        params.put("universeId", universeId);
        params.put("memberId", memberId);

        if (likeMapper.checkLike(params) == 0) {
            likeMapper.insertLike(params);
        }
    }

    // 무조건 삭제 (없으면 무시)
    public void deleteLike(Long universeId, String memberId) {
    	
        Map<String, Object> params = new HashMap<>();
        params.put("universeId", universeId);
        params.put("memberId", memberId);

        if (likeMapper.checkLike(params) > 0) {
            likeMapper.deleteLike(params);
        }
    }
}