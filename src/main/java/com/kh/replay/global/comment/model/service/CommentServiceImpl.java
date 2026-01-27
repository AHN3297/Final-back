package com.kh.replay.global.comment.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.comment.model.dao.CommentMapper;
import com.kh.replay.global.comment.model.dto.CommentCreateRequest;
import com.kh.replay.global.comment.model.dto.CommentDTO;
import com.kh.replay.global.exception.ForbiddenException;
import com.kh.replay.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

	private final CommentMapper commentMapper;

	@Override
	public CommentDTO createComment(String targetType, Long targetId, CommentCreateRequest request, String userId) {
		CommentDTO comment = CommentDTO.builder()
				.content(request.getContent())
				.targetType(targetType)
				.targetId(targetId)
				.memberId(userId)
				.build();

		int result = commentMapper.insertComment(comment);

		if (result == 0) {
			throw new RuntimeException("댓글 생성에 실패했습니다.");
		}

		return commentMapper.findByCommentId(comment.getCommentId());
	}

	@Override
	public CommentDTO updateComment(Long commentId, CommentCreateRequest request, String userId) {
		CommentDTO existing = findByCommentId(commentId);
		validateOwner(existing, userId);

		CommentDTO update = CommentDTO.builder()
				.commentId(commentId)
				.content(request.getContent())
				.build();

		commentMapper.updateComment(update);

		return commentMapper.findByCommentId(commentId);
	}

	private CommentDTO findByCommentId(Long commentId) {
		CommentDTO comment = commentMapper.findByCommentId(commentId);
		if (comment == null) {
			throw new ResourceNotFoundException("해당 댓글을 찾을 수 없습니다.");
		}
		return comment;
	}

	private void validateOwner(CommentDTO comment, String userId) {
		if (!comment.getMemberId().equals(userId)) {
			throw new ForbiddenException("해당 댓글에 대한 권한이 없습니다.");
		}
	}

}
