package com.kh.replay.universe.model.dto;

import jakarta.validation.constraints.NotBlank;
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
public class UniverseCreateRequest {

	@NotBlank(message = "유니버스 제목은 필수 입력 값입니다.")
	private String title;
	private String layoutData;
	private String themeCode;
	private String memberId;
	private String status; 

}
