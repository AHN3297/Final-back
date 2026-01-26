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
public class ShortformCommentDTO {

	private Long commentId;
	private String content;
	private Long shortFormId;
	private String memberId;
	private String nickName;
	private String status;
	private LocalDateTime createdAt;

}
