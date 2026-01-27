package com.kh.replay.global.comment.model.dto;

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
public class CommentDTO {

	private Long commentId;
	private String content;
	private String targetType;
	private Long targetId;
	private String memberId;
	private String nickName;
	private String status;
	private LocalDateTime createdAt;

}
