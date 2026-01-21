package com.kh.replay.global.notice.model.domain;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Notice {
	
	private Long noticeNo;
	private String noticeTitle;
	private String noticeContent;
	private String memberId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private String status;
	
	public void updateNotice(String noticeTitle, String noticeContent) {
		this.noticeTitle = noticeTitle;
		this.noticeContent = noticeContent;
		this.updatedAt = LocalDateTime.now();
	}
	
}
