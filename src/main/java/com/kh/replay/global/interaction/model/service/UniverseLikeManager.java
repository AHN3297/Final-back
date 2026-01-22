package com.kh.replay.global.interaction.model.service;

import org.springframework.stereotype.Component;
import com.kh.replay.global.interaction.model.dao.InteractionMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UniverseLikeManager {

    private final InteractionMapper interactionMapper;

    /**
     * 좋아요 토글 실행 
     * @return true: 좋아요 추가, false: 좋아요 취소
     */
    public boolean executeToggle(Long universeId, String memberId) {
        // 1. 좋아요 상태확인
        if (interactionMapper.checkLike(universeId, memberId) > 0) {
            // 있으면 삭제 (취소)
            interactionMapper.deleteLike(universeId, memberId);
            return false;
        } else {
            // 없으면 추가 (등록)
            interactionMapper.insertLike(universeId, memberId);
            return true;
        }
    }
}