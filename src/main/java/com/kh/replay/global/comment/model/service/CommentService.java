package com.kh.replay.global.comment.model.service;

import com.kh.replay.global.comment.model.dto.CommentCreateRequest;
import com.kh.replay.global.comment.model.dto.CommentDTO;

public interface CommentService {

	CommentDTO createComment(String targetType, Long targetId, CommentCreateRequest request, String userId);

	CommentDTO updateComment(Long commentId, CommentCreateRequest request, String userId);

}
