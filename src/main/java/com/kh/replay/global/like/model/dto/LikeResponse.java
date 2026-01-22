package com.kh.replay.global.like.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeResponse {

	private Long targetId;
	private String type;
	private boolean isLiked;
	private int likeCount;

}
