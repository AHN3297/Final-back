package com.kh.replay.global.interaction.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.interaction.model.dao.InteractionMapper;
import com.kh.replay.global.interaction.model.dto.LikeResponse;
import com.kh.replay.universe.model.dao.UniverseMapper;
import com.kh.replay.universe.model.service.UniverseValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InteractionServiceImpl implements InteractionService {

    private final UniverseMapper universeMapper;
    private final InteractionMapper interactionMapper;
    private final UniverseValidator validator;
    private final UniverseLikeManager likeManager; 

    @Override
    public LikeResponse toggleLike(Long universeId, String memberId) {
        
    	//유니버스 조회
        validator.validateExisting(universeId);

        //좋아요 상태값 확인
        boolean isLiked = likeManager.executeToggle(universeId, memberId);
        
        //좋아요 갯수 조회
        int totalLikes = countLikes(universeId);
        
        //반환값 생성
        return LikeResponse.builder()
                .targetId(universeId)
                .isLiked(isLiked)
                .likeCount(totalLikes)
                .build();
    }
    
    
    //좋아요 갯수 조회용 메소드
    private int countLikes(Long universeId) {
    	
    	int totalLikes = interactionMapper.countLikes(universeId);
    	
    	return totalLikes;
    }

}