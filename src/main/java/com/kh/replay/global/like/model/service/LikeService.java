package com.kh.replay.global.like.model.service;

import com.kh.replay.global.like.model.dto.LikeResponse;

public interface LikeService {

	//유니버스 좋아요 생성
    LikeResponse likeUniverse(Long universeId, String memberId);

    //유니버스 좋아요 삭제
    LikeResponse unlikeUniverse(Long universeId, String memberId);
    

}