package com.kh.replay.global.comment.model.dto;

import java.util.List;

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
public class CommentListResponse {

	private List<CommentDTO> content;
	private Pagination pagination;

	@Getter
	@Builder
	public static class Pagination {
		private boolean hasNext;
		private Long lastCommentId;
		private int size;
	}

}
