package com.kh.replay.global.like.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.kh.replay.global.like.model.dao.LikeMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShortformLikeService {

	private final LikeMapper likeMapper;

	public void createLike(Long shortFormId, String memberId) {
		Map<String, Object> params = new HashMap<>();
		params.put("shortFormId", shortFormId);
		params.put("memberId", memberId);

		if (likeMapper.checkShortformLike(params) == 0) {
			likeMapper.insertShortformLike(params);
		}
	}

	public void deleteLike(Long shortFormId, String memberId) {
		Map<String, Object> params = new HashMap<>();
		params.put("shortFormId", shortFormId);
		params.put("memberId", memberId);

		if (likeMapper.checkShortformLike(params) > 0) {
			likeMapper.deleteShortformLike(params);
		}
	}

}
