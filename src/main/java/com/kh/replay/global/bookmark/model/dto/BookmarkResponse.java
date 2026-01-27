package com.kh.replay.global.bookmark.model.dto;

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
public class BookmarkResponse {

	private Long targetId;
	private String type;
	private boolean isBookmark;
	private int bookmarkCount;

}
