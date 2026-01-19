package com.kh.replay.global.notice.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticeListResponseDto {

	private int page;
	private int size;
	private Long totalElements;
	private int totalPages;
	private List<NoticeItemDto> items;                           
}
