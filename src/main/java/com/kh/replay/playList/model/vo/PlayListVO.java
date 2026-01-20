package com.kh.replay.playList.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayListVO {
	private int playListId;
	private String playListName; 
	private Date createdAt;
	private String memberId;
	private Date updateAt;
	
	

}
