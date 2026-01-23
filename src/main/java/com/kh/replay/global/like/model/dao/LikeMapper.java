package com.kh.replay.global.like.model.dao; 

import org.apache.ibatis.annotations.Mapper; 
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {

    int checkLike(@Param("universeId") Long universeId, @Param("memberId") String memberId);
    
    int insertLike(@Param("universeId") Long universeId, @Param("memberId") String memberId);
    
    int deleteLike(@Param("universeId") Long universeId, @Param("memberId") String memberId);
    
    int countLikes(Long universeId);
    

}