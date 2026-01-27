package com.kh.replay.global.comment.model.service;

import com.kh.replay.global.comment.model.dto.CommentCreateRequest;
import com.kh.replay.global.comment.model.dto.CommentDTO;
import com.kh.replay.global.comment.model.dto.CommentListResponse;

public interface CommentService {

	CommentListResponse findAllComments(String targetType, Long targetId, int size, Long lastCommentId);

	CommentDTO createComment(String targetType, Long targetId, CommentCreateRequest request, String userId);

	CommentDTO updateComment(Long commentId, CommentCreateRequest request, String userId);

}
