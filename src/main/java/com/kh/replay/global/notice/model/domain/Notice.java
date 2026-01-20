package com.kh.replay.global.notice.model.domain;

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
	
	// 공지사항 번호(PK)
	private Long noticeNo;
	// 제목
	private String title;
	// 내용
	private String content;
	// 이미지 경로
	private String imgPath;
	// 생성일
	private LocalDateTime createdAt;
	// 수정일
	private LocalDateTime updatedAt;
	// 조회수
	private int viewCount;
	// 상태 ( Y / N 등 )
	private String status;
	
	public void updateNotice(String title, String content, String imgPath) {
		this.title = title;
		this.content = content;
		this.imgPath = imgPath;
		this.updatedAt = LocalDateTime.now();
	}
	
}
