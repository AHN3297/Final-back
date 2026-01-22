package com.kh.replay.global.bookmark.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookmarkMapper {


    int checkBookmark(@Param("universeId") Long universeId, @Param("memberId") String memberId);

    int insertBookmark(@Param("universeId") Long universeId, @Param("memberId") String memberId);

    int deleteBookmark(@Param("universeId") Long universeId, @Param("memberId") String memberId);

    int countBookmark(@Param("universeId") Long universeId);

}