package com.kh.replay.global.interaction.model.service;

import com.kh.replay.global.interaction.model.dto.LikeResponse;

public interface InteractionService {

	//유니버스 좋아요 생성
    LikeResponse likeUniverse(Long universeId, String memberId);

    //유니버스 좋아요 삭제
    LikeResponse unlikeUniverse(Long universeId, String memberId);


}