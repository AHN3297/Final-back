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
	private String thumbnailUrl;  
	
	private String memberId; 
	private String nickName; 
	
	private long likeCount;  
	private long bookmarkCount; 
	
	private String mainPlaylist;
	 
	private LocalDateTime createdAt;

}
