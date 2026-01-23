package com.kh.replay.global.like.model.dao; 

import java.util.Map;
import org.apache.ibatis.annotations.Mapper; 

@Mapper
public interface LikeMapper {

    int checkLike(Map<String, Object> params);
    
    int insertLike(Map<String, Object> params);
    
    int deleteLike(Map<String, Object> params);
    
    int countLikes(Long universeId);
    

}