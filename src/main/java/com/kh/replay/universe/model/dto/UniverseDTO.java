package com.kh.replay.universe.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UniverseDTO {
	
	private Long universeId;
	private String title;
	private String layoutData;
	private String themeCode;
	private String status;
	private String thumbnailUrl; //썸내일 
	
	private String memberId; //맴버태그
	private String nickName; //별명
	
	private long like; //좋아요 
	private long bookmark; // 북마크
	
	private String mainPlaylist;
	 
	private LocalDateTime createAt;

}
