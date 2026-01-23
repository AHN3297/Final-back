package com.kh.replay.notice.model.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeImg {

	private Long imgId;
	private String originName;
	private String changeName;
	private String status;
	private Long noticeNo;
}
