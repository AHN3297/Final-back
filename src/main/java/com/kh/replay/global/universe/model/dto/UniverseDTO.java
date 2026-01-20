package com.kh.replay.global.universe.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniverseDTO {
	
	private Long universeId;
	private String title;
	private String layoutData;
	private String themeCode;
	private String thumbnailUrl; //썸내일 
	
	private String memberId; //맴버태그
	private String NickName; //별명
	
	private long like; //좋아요 
	private long bookmark; // 북마크
	
	private String mainPlaylist;
	 
	private LocalDateTime createAt;

}
