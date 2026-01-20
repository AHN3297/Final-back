package com.kh.replay.global.universe.model.dto;

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
public class UniverseCreateRequest {
	
	private String title;
	private String layoutData;
	private String themeCode;
	private String memberId;

}
