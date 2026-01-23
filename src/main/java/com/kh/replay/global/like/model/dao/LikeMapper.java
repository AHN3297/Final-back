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
    
    // apiId로 좋아하는 가수 조회
    Integer findSingerNoByApiId(Long apiSingerId);
    
    // 가수 정보 insert
    void insertArtistApiInfo(LikeArtistVO artistVo);
    
    // 가수 중복 조회
    int checkArtistLikeExists(Map<String, Object> params);
    
    // 좋아하는 가수 insert (중간테이블)
    int insertFavoriteArtist(Map<String, Object> params);
    
    // 좋아하는 가수 삭제 (중간테이블만 삭제)
    int deleteArtistLike(Map<String, Object> params);


    

}