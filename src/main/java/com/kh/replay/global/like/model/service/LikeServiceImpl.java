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
import com.kh.replay.shortform.model.service.ShortformValidator;
import com.kh.replay.universe.model.service.UniverseValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper; 
    
    private final UniverseValidator universeValidator;
    private final ShortformValidator shortformValidator;
    
    private final UniverseLikeService universeLikeService;
    private final ShortformLikeService shortformLikeService;
    private final ArtistLikeService artistLikeService;
    private final TrackLikeService trackLikeService;

    // 1. 유니버스 (Universe)
    
    @Override
    public LikeResponse likeUniverse(Long universeId, String memberId) {
        // 1. 유니버스 존재 확인
        universeValidator.validateExisting(universeId);
        
        // 2. 유니버스 좋아요 생성 
        universeLikeService.createLike(universeId, memberId); 
        
        // 3. 총 개수 조회
        int totalLikes = likeMapper.countUniverseLikes(universeId);
        
        return LikeResponse.builder()
                .targetId(universeId)
                .type("UNIVERSE")
                .isLiked(true)
                .likeCount(totalLikes)
                .build();
    }

    @Override
    public LikeResponse unlikeUniverse(Long universeId, String memberId) {
        // 1. 유니버스 존재 확인
        universeValidator.validateExisting(universeId);
        
        // 2. 유니버스 좋아요 삭제 
        universeLikeService.deleteLike(universeId, memberId);
        
        // 3. 총 개수 조회
        int totalLikes = likeMapper.countUniverseLikes(universeId);
        
        return LikeResponse.builder()
                .targetId(universeId)
                .type("UNIVERSE") 
                .isLiked(false)
                .likeCount(totalLikes)
                .build();
    }

    // 2. 숏폼 (ShortForm)

    @Override
    public LikeResponse likeShortform(Long shortFormId, String memberId) {
        // 1. 숏폼 존재 확인
        shortformValidator.validateExisting(shortFormId);
        
        // 2. 숏폼 좋아요 생성
        shortformLikeService.createLike(shortFormId, memberId);
        
        // 3. 총 개수 조회
        int totalLikes = likeMapper.countShortformLikes(shortFormId);
        
        return LikeResponse.builder()
                .targetId(shortFormId)
                .type("SHORTFORM")
                .isLiked(true)
                .likeCount(totalLikes)
                .build();
    }

    @Override
    public LikeResponse unlikeShortform(Long shortFormId, String memberId) {
        // 1. 숏폼 존재 확인
        shortformValidator.validateExisting(shortFormId);
        
        // 2. 숏폼 좋아요 삭제
        shortformLikeService.deleteLike(shortFormId, memberId);
        
        // 3. 총 개수 조회
        int totalLikes = likeMapper.countShortformLikes(shortFormId);
        
        return LikeResponse.builder()
                .targetId(shortFormId)
                .type("SHORTFORM")
                .isLiked(false)
                .likeCount(totalLikes)
                .build();
    }
    
    // 3. 아티스트 (Artist)

    @Override
    public LikeResponse likeArtist(ArtistDTO artistDto, String memberId) {
        // API 정보 저장 및 PK 반환
        int singerNo = artistLikeService.saveArtistApiInfo(artistDto);
        
        // 좋아요 테이블에 추가
        artistLikeService.createFavoriteArtist(singerNo, memberId);
        
        return LikeResponse.builder()
                .targetId(artistDto.getApiSingerId())
                .type("ARTIST")
                .isLiked(true)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LikeArtistVO> findAllFavoriteArtists(String memberId) {
        return likeMapper.selectFavoriteArtists(memberId);
    }
    
    @Override
    public LikeResponse unlikeArtist(int singerNo, String memberId) {
        artistLikeService.deleteFavoriteArtist(singerNo, memberId);
        
        return LikeResponse.builder()
                .targetId((long)singerNo)
                .isLiked(false)
                .build();
    }
    
    // 4. 노래 (Track)
    
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
        return trackLikeService.removeFavorite(memberId, songNo);
    }

    @Override
    @Transactional
    public int reorderTracks(String memberId, List<LikeTrackVO> newOrderList) {
        int totalUpdatedRows = 0;
        // 변경된 순서 리스트를 받아 순차 업데이트
        for (LikeTrackVO track : newOrderList) {
            int result = trackLikeService.changeOrder(memberId, track.getSongNo(), track.getTrackOrder());
            totalUpdatedRows += result;
        }
        return totalUpdatedRows;
    }
}