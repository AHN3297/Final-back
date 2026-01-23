package com.kh.replay.notice.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDetailResponseDto {

	private Long noticeNo;
	private String title;
	private String content;
	private String memberId;
	private String status;
	private String createdAt;
	private String updatedAt;
	
	private List<String> images;
}
