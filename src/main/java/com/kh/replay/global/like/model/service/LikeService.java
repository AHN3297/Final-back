package com.kh.replay.global.like.model.service;

import java.util.List;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.api.model.dto.MusicDTO;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.global.like.model.vo.LikeArtistVO;
import com.kh.replay.global.like.model.vo.LikeTrackVO;

public interface LikeService {

	// 유니버스 좋아요 생성
    LikeResponse likeUniverse(Long universeId, String memberId);

    // 유니버스 좋아요 삭제
    LikeResponse unlikeUniverse(Long universeId, String memberId);
    
    // 좋아하는 아티스트 생성/추가
    LikeResponse likeArtist(ArtistDTO artistDto, String memberId);
    
    // 좋아하는 아티스트 조회
    List<LikeArtistVO> findAllFavoriteArtists(String memberId);
    
    // 좋아하는 아티스트 삭제
    LikeResponse unlikeArtist(int singerNo, String memberId);

    // 좋아하는 노래 생성/추가 
	LikeTrackVO likeTrack(MusicDTO musicDto, String username);

	List<LikeTrackVO> getMyLikes(String memberId);

	LikeResponse deleteLike(String memberId, Long songNo);

	int reorderTracks(String memberId, List<LikeTrackVO> newOrderList);
    
    
    // 좋아하는 노래 조회
    
    // 좋아하는 노래 삭제
    

}