package com.kh.replay.global.comment.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.comment.model.dao.CommentMapper;
import com.kh.replay.global.comment.model.dto.CommentCreateRequest;
import com.kh.replay.global.comment.model.dto.CommentDTO;
import com.kh.replay.global.comment.model.dto.CommentListResponse;
import com.kh.replay.global.exception.ForbiddenException;
import com.kh.replay.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

	private final CommentMapper commentMapper;

	@Override
	@Transactional(readOnly = true)
	public CommentListResponse findAllComments(String targetType, Long targetId, int size, Long lastCommentId) {
		Map<String, Object> params = new HashMap<>();
		params.put("targetId", targetId);
		params.put("lastCommentId", lastCommentId);
		params.put("limit", size + 1);

		List<CommentDTO> list = commentMapper.findAllComments(params);
		return buildResponse(list, size);
	}

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

	@Override
	public void deleteComment(Long commentId, String userId) {
		CommentDTO existing = findByCommentId(commentId);
		validateOwner(existing, userId);

		commentMapper.deleteComment(commentId);
	}

	@Override
	@Transactional(readOnly = true)
	public CommentListResponse findMyComments(String memberId, int size, Long lastCommentId) {
		Map<String, Object> params = new HashMap<>();
		params.put("memberId", memberId);
		params.put("lastCommentId", lastCommentId);
		params.put("limit", size + 1);

		List<CommentDTO> list = commentMapper.findMyComments(params);
		return buildResponse(list, size);
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

	private CommentListResponse buildResponse(List<CommentDTO> list, int size) {
		boolean hasNext = false;
		if (list.size() > size) {
			hasNext = true;
			list.remove(size);
		}

		Long nextLastCommentId = null;
		if (!list.isEmpty()) {
			nextLastCommentId = list.get(list.size() - 1).getCommentId();
		}

		CommentListResponse.Pagination pagination = CommentListResponse.Pagination.builder()
				.hasNext(hasNext)
				.lastCommentId(nextLastCommentId)
				.size(size)
				.build();

		return CommentListResponse.builder()
				.content(list)
				.pagination(pagination)
				.build();
	}

}
