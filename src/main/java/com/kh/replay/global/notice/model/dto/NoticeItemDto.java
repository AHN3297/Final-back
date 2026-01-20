package com.kh.replay.global.notice.model.dto;

import java.time.format.DateTimeFormatter;

import com.kh.replay.global.notice.model.domain.Notice;

import lombok.Getter;

@Getter
public class NoticeItemDto {
	
	private Long noticeNo;
	private String title;
	private String createdAt;
	private String status;
	
	public NoticeItemDto(Notice notice) {
		this.noticeNo = notice.getNoticeNo();
		this.title = notice.getTitle();
		this.createdAt = notice.getCreatedAt() != null ? notice.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
		this.status = notice.getStatus();
	}

}
