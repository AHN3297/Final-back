package com.kh.replay.shortform.model.dto;

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
public class ShortformCreateRequest {

	private String shortFormTitle;
	private String caption;
	private String status;

}
