package com.kh.replay.global.comment.model.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.global.comment.model.dto.CommentDTO;

@Mapper
public interface CommentMapper {

	CommentDTO findByCommentId(Long commentId);

	int insertComment(CommentDTO comment);

	int updateComment(CommentDTO comment);

}
