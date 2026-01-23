package com.kh.replay.global.like.model.dao; 

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.global.like.model.vo.LikeArtistVO; 

@Mapper
public interface LikeMapper {

    int checkLike(Map<String, Object> params);
    
    int insertLike(Map<String, Object> params);
    
    int deleteLike(Map<String, Object> params);
    
    int countLikes(Long universeId);
    
    // 좋아하는가수
    Integer findSingerNoByApiId(Long apiSingerId);
    void insertArtistApiInfo(LikeArtistVO artistVo);
    int checkArtistLikeExists(Map<String, Object> params);
    int insertFavoriteArtist(Map<String, Object> params);
    int deleteArtistLike(Map<String, Object> params);


    

}