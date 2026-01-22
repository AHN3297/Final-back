package com.kh.replay.global.interaction.model.dao; 

import org.apache.ibatis.annotations.Mapper; 
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InteractionMapper {

    int checkLike(@Param("universeId") Long universeId, @Param("memberId") String memberId);
    
    int insertLike(@Param("universeId") Long universeId, @Param("memberId") String memberId);
    
    int deleteLike(@Param("universeId") Long universeId, @Param("memberId") String memberId);
    int countLikes(Long universeId);
    

}