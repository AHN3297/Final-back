package com.kh.replay.playList.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayListDTO {
	
	private int playListId;
	private String playListName; 	// requestBody
	private Date createdAt;
	private String memberId;
	private Date updateAt;

}
