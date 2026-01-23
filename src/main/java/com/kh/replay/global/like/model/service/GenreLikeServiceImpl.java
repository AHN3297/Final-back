package com.kh.replay.global.like.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.exception.ResourceNotFoundException;
import com.kh.replay.global.like.model.dao.LikeMapper;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.global.like.model.vo.LikeGenreVO;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class GenreLikeServiceImpl implements GenreLikeService {
	
	private final LikeMapper likeMapper;
	
	@Transactional
	@Override
	public LikeResponse likeGenre(String memberId, String genreName) {
		
		if(genreName == null || genreName.trim().isEmpty()) {
			throw new IllegalArgumentException("장르 형식이 잘못되었습니다. 다시 시도해주세요.");
		}
		
		// 1. genreName -> genreId 조회
		Long genreId = likeMapper.findGenreIdByName(genreName.trim());
		if(genreId == null) {
			throw new ResourceNotFoundException("장르 정보가 없습니다.");
		}
		
		// 2. 중복 체크
		int exists = likeMapper.existsMemberGenre(memberId, genreId);
		if(exists > 0) {
			throw new IllegalArgumentException("이미 추가된 장르입니다.");
		}
		
		// 3. insert
		LikeGenreVO vo = new LikeGenreVO();
		vo.setMemberId(memberId);
		vo.setGenreId(genreId);
		
		likeMapper.insertMemberGenre(vo);
		
		// 4. 응답 조립
		return LikeResponse.builder()
				.targetId(genreId)
				.type("GENRE")
				.isLiked(true)
				.build();
	}
}
