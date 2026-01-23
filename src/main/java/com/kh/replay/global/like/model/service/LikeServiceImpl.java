package com.kh.replay.global.like.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.like.model.dao.LikeMapper;
import com.kh.replay.global.like.model.dto.LikeResponse;
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
    
    // 좋아하는 아티스트 삭제
    @Override
    public LikeResponse unlikeArtist(int singerNo, String memberId) {
      
        artistLikeService.deleteFavoriteArtist(singerNo, memberId);
        
        return LikeResponse.builder()
                .targetId((long)singerNo)
                .isLiked(false)
                .build();
    }
}