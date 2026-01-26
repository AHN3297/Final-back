package com.kh.replay.global.like.model.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeArtistVO {
	private int singerNo;
	private Long apiSingerId;
	private String apiSingerName;
	private String singerGenre;
	private String singerImgUrl;
}
