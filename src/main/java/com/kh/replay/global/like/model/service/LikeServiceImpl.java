package com.kh.replay.global.like.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.like.model.dao.LikeMapper;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.universe.model.service.UniverseValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final UniverseValidator validator;
    private final UniverseLikeManager likeManager; 

    //좋아요 추가 
    @Override
    public LikeResponse likeUniverse(Long universeId, String memberId) {
        validator.validateExisting(universeId);
        
        likeManager.createLike(universeId, memberId); 
        
        int totalLikes = countLikes(universeId);
        
        return LikeResponse.builder()
                .targetId(universeId)
                .isLiked(true) // 무조건 true
                .likeCount(totalLikes)
                .build();
    }

    // 좋아요 취소 
    @Override
    public LikeResponse unlikeUniverse(Long universeId, String memberId) {
        validator.validateExisting(universeId);
        
        
        likeManager.deleteLike(universeId, memberId);
        
        int totalLikes = countLikes(universeId);
        
        return LikeResponse.builder()
                .targetId(universeId)
                .isLiked(false) // 무조건 false
                .likeCount(totalLikes)
                .build();
    }
    
    
    //좋아요 갯수 조회용 메소드 추후 여러군대 사용가능성
    private int countLikes(Long universeId) {
    	
    	int totalLikes = likeMapper.countLikes(universeId);
    	
    	return totalLikes;
    }

}