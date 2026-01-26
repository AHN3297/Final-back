package com.kh.replay.global.like.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.replay.global.exception.DuplicateException;
import com.kh.replay.global.exception.NotFoundOrderListException;
import com.kh.replay.global.exception.NotFoundTracksException;
import com.kh.replay.global.like.model.dao.LikeMapper;
import com.kh.replay.global.like.model.vo.LikeTrackVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TrackLikeService {  
    private final LikeMapper likeMapper;

    public Long getOrInsertTrack(LikeTrackVO trackVO) {
        // 1. 이미 등록된 노래인지 확인 (Long 타입으로 조회)
        Long songNo = likeMapper.selectSongNoByApiId(trackVO.getTrackId());

        // 2. 없는 노래라면 INSERT 후 생성된 PK(Long) 반환
        if (songNo == null) {
            likeMapper.insertApiSong(trackVO);
            return trackVO.getSongNo();
        }
        return songNo;
    }

    public int addTrackToFavorite(String memberId, Long songNo) {
        int result = likeMapper.checkSongLikeExists(memberId, songNo);
        
        if (result > 0) {
        	throw new DuplicateException("이미 좋아하는 음악에 담겨있습니다.");
        }
        return likeMapper.insertFavoriteSong(memberId, songNo);
    }
    
    public List<LikeTrackVO> getFavoriteTracks(String memberId) {
    	// 조회 결과 없으면 빈 배열을 보여줌
    	return likeMapper.selectFavoriteTracks(memberId);
    }

    public int removeFavorite(String memberId, Long songNo) {
        int result = likeMapper.deleteFavoriteSong(memberId, songNo);
        if(result == 0) {
        	throw new NotFoundTracksException("삭제할 노래 정보를 찾을 수 없습니다.");
        }
        return result;
    }

    public int changeOrder(String memberId, Long songNo, int newOrder) {
        int result = likeMapper.updateTrackOrder(memberId, songNo, newOrder);
        if(result == 0) {
        	throw new NotFoundOrderListException("순서 정보를 업데이트할 노래를 찾을 수 없습니다.");
        }
        return result;
    }
    
    
}
