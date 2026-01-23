package com.kh.replay.global.like.model.dao; 

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.replay.global.like.model.vo.LikeGenreVO; 

@Mapper
public interface LikeMapper {

    int checkLike(Map<String, Object> params);
    
    int insertLike(Map<String, Object> params);
    
    int deleteLike(Map<String, Object> params);
    
    int countLikes(Long universeId);
    
    
    // 장르 선택 인터페이스
    Long findGenreIdByName(String genreName);
    
    int existsMemberGenre(@Param("memberId") String memberId, 
    					  @Param("genreId") Long genreId);
    
    int insertMemberGenre(LikeGenreVO vo);
    

}