package com.kh.replay.global.like.model.service;

import com.kh.replay.global.like.model.dto.LikeResponse;

public interface GenreLikeService {
	
	LikeResponse likeGenre(String memberId, String genreName);

}
