package com.kh.replay.global.interaction.model.service;

import com.kh.replay.global.interaction.model.dto.LikeResponse;

public interface InteractionService {

	LikeResponse toggleLike(Long universeId, String userId);

}
