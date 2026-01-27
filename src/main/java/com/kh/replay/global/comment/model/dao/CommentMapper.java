package com.kh.replay.global.comment.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.global.comment.model.dto.CommentDTO;

@Mapper
public interface CommentMapper {

	List<CommentDTO> findAllComments(Map<String, Object> params);

	CommentDTO findByCommentId(Long commentId);

	int insertComment(CommentDTO comment);

	int updateComment(CommentDTO comment);

	int deleteComment(Long commentId);

	List<CommentDTO> findMyComments(Map<String, Object> params);

}
