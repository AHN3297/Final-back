package com.kh.replay.notice.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeUpdateRequestDto {
	
	private String noticeTitle;
	private String noticeContent;
	private String deleteImgIds;
	private List<MultipartFile> files;
}
