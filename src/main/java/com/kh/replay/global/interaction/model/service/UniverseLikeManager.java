package com.kh.replay.global.interaction.model.service;

import org.springframework.stereotype.Component;
import com.kh.replay.global.interaction.model.dao.InteractionMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UniverseLikeManager {

    private final InteractionMapper interactionMapper;
    
    public void createLike(Long universeId, String memberId) {
        if (interactionMapper.checkLike(universeId, memberId) == 0) {
            interactionMapper.insertLike(universeId, memberId);
        }
    }

    // 무조건 삭제 (없으면 무시)
    public void deleteLike(Long universeId, String memberId) {
        if (interactionMapper.checkLike(universeId, memberId) > 0) {
            interactionMapper.deleteLike(universeId, memberId);
        }
    }
}