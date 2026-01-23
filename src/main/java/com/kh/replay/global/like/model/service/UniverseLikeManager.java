package com.kh.replay.global.like.model.service;

import org.springframework.stereotype.Component;

import com.kh.replay.global.like.model.dao.LikeMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UniverseLikeManager {

    private final LikeMapper likeMapper;
    
    public void createLike(Long universeId, String memberId) {
        if (likeMapper.checkLike(universeId, memberId) == 0) {
        	likeMapper.insertLike(universeId, memberId);
        }
    }

    // 무조건 삭제 (없으면 무시)
    public void deleteLike(Long universeId, String memberId) {
        if (likeMapper.checkLike(universeId, memberId) > 0) {
        	likeMapper.deleteLike(universeId, memberId);
        }
    }
}