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

    /**
     * 7. 좋아요 토글
     */
    @Override
    public LikeResponse toggleLike(Long universeId, String memberId) {
        
        // 1. 유니버스 존재 확인
        validator.validateExisting(universeId);

        // 2. 좋아요 여부 확인 & 토글
        int count = interactionMapper.checkLike(universeId, memberId);
        boolean isLiked;

        if (count > 0) {
        	interactionMapper.deleteLike(universeId, memberId);
            isLiked = false;
        } else {
        	interactionMapper.insertLike(universeId, memberId);
            isLiked = true;
        }

        // 3. 갱신된 총 좋아요 수 조회
        int totalLikes = interactionMapper.countLikes(universeId);

        return LikeResponse.builder()
                .targetId(universeId)
                .isLiked(isLiked)
                .likeCount(totalLikes)
                .build();
    }

}