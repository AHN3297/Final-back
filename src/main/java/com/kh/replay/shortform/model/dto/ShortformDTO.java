package com.kh.replay.shortform.model.dto;

import java.time.LocalDateTime;

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
public class ShortformDTO {

	private Long shortFormId;
	private String shortFormTitle;
	private String videoUrl;
	private String thumbnailUrl;
	private String caption;
	private Long duration;

	private String memberId;
	private String nickName;
	private String status;

	private long like;

	private LocalDateTime createdAt;

}
