package com.kh.replay.global.like.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.like.model.dao.LikeMapper;
import com.kh.replay.global.like.model.vo.LikeArtistVO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArtistLikeService {

    private final LikeMapper likeMapper;

    // 가수 정보 저장 로직
    public int saveArtistApiInfo(ArtistDTO dto) {
        LikeArtistVO vo = LikeArtistVO.builder()
                .apiSingerId(dto.getApiSingerId())
                .apiSingerName(dto.getApiSingerName())
                .singerGenre(dto.getSingerGenre())
                .singerImgUrl(dto.getSingerImgUrl())
                .build();

        Integer singerNo = likeMapper.findSingerNoByApiId(vo.getApiSingerId());
        if (singerNo == null) {
            likeMapper.insertArtistApiInfo(vo); // useGeneratedKeys로 vo에 singerNo 채워짐
            singerNo = vo.getSingerNo();
        }
        return singerNo;
    }

    // 좋아요 등록 로직 (RuntimeException 롤백 유도)
    public void createFavoriteArtist(int singerNo, String memberId) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("singerNo", singerNo);

        if(likeMapper.checkArtistLikeExists(params) > 0) {
        	throw new RuntimeException("이미 좋아하는 아티스트입니다.");
        }
      
        int result = likeMapper.insertFavoriteArtist(params);
        if (result <= 0) throw new RuntimeException("좋아요 등록 실패");
        
    }

    // 좋아요 삭제 로직
    public void deleteFavoriteArtist(int singerNo, String memberId) {
        Map<String, Object> params = new HashMap<>();
        params.put("memberId", memberId);
        params.put("singerNo", singerNo);

        int result = likeMapper.deleteArtistLike(params);
        if (result <= 0) throw new RuntimeException("좋아요 삭제 실패");
    }
}
