package com.kh.replay.global.like.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.api.model.dto.MusicDTO;
import com.kh.replay.global.like.model.dao.LikeMapper;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.global.like.model.vo.LikeArtistVO;
import com.kh.replay.global.like.model.vo.LikeTrackVO;
import com.kh.replay.universe.model.service.UniverseValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper; // 카운트 조회용
    private final UniverseValidator validator;
    private final UniverseLikeService universeLikeService; // 매니저 추가
    private final ArtistLikeService artistLikeService;
    private final TrackLikeService trackLikeService;

    // 좋아요 추가
    @Override
    public LikeResponse likeUniverse(Long universeId, String memberId) {
        // 1. 유니버스 존재 확인
        validator.validateExisting(universeId);
        
        // 2. 매니저에게 위임
        universeLikeService.createLike(universeId, memberId); 
        
        // 3. 총 개수 조회
        int totalLikes = countLikes(universeId);
        
        return LikeResponse.builder()
                .targetId(universeId)
                .isLiked(true)
                .likeCount(totalLikes)
                .build();
    }

    // 좋아요 취소
    @Override
    public LikeResponse unlikeUniverse(Long universeId, String memberId) {
        // 1. 유니버스 존재 확인
        validator.validateExisting(universeId);
        
        // 2. 매니저에게 위임
        universeLikeService.deleteLike(universeId, memberId);
        
        // 3. 총 개수 조회
        int totalLikes = countLikes(universeId);
        
        return LikeResponse.builder()
                .targetId(universeId)
                .isLiked(false)
                .likeCount(totalLikes)
                .build();
    }
    
    // 좋아요 갯수 조회용 내부 메서드
    private int countLikes(Long universeId) {
        return likeMapper.countLikes(universeId);
    }
    
    // 좋아하는 아티스트 추가
    @Override
    public LikeResponse likeArtist(ArtistDTO artistDto, String memberId) {
        
        int singerNo = artistLikeService.saveArtistApiInfo(artistDto);

        
        artistLikeService.createFavoriteArtist(singerNo, memberId);
        
        return LikeResponse.builder()
                .targetId(artistDto.getApiSingerId())
                .type("ARTIST")
                .isLiked(true)
                .build();
    }
    
    // 좋아하는 아티스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<LikeArtistVO> findAllFavoriteArtists(String memberId) {
        // 이제 JwtFilter에서 정상적으로 전달된 memberId를 기반으로 조회합니다.
        return likeMapper.selectFavoriteArtists(memberId);
    }
    
    // 좋아하는 아티스트 삭제
    @Override
    public LikeResponse unlikeArtist(int singerNo, String memberId) {
      
        artistLikeService.deleteFavoriteArtist(singerNo, memberId);
        
        return LikeResponse.builder()
                .targetId((long)singerNo)
                .isLiked(false)
                .build();
    }
    
    
    // 좋아하는 노래 추가
    @Override
    @Transactional(rollbackFor = Exception.class)
    public LikeTrackVO likeTrack(MusicDTO musicDto, String memberId) { 
        LikeTrackVO trackVO = LikeTrackVO.builder()
                .trackId(musicDto.getTrackId())     
                .artistId(musicDto.getArtistId())   
                .title(musicDto.getTitle())
                .artistName(musicDto.getArtistName())
                .album(musicDto.getAlbum())
                .genreName(musicDto.getGenreName())
                .duration(String.valueOf(musicDto.getDuration())) 
                .releaseDate(musicDto.getReleaseDate())
                .coverImgUrl(musicDto.getCoverImgUrl())
                .previewUrl(musicDto.getPreviewUrl())
                .build();

        // DB에서 songNo를 가져오거나 생성
        Long songNo = trackLikeService.getOrInsertTrack(trackVO);
        
        // 생성된(혹은 조회된) PK를 객체에 넣어줌
        trackVO.setSongNo(songNo);

        // 좋아요 테이블 등록
        trackLikeService.addTrackToFavorite(memberId, songNo);

        return trackVO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LikeTrackVO> getMyLikes(String memberId) {
        return trackLikeService.getFavoriteTracks(memberId);
    }

    @Override
    @Transactional
    public int deleteLike(String memberId, Long songNo) {
        int result = trackLikeService.removeFavorite(memberId, songNo);
        return result;
    }

    @Override
    @Transactional
    public int reorderTracks(String memberId, List<LikeTrackVO> newOrderList) {
    	int totalUpdatedRows = 0;
        // 리액트에서 변경된 순서가 담긴 리스트를 보내주면 순차적으로 업데이트
        for (LikeTrackVO track : newOrderList) {
            int result = trackLikeService.changeOrder(memberId, track.getSongNo(), track.getTrackOrder());
            totalUpdatedRows += result;
        }
		return totalUpdatedRows;
    }
}