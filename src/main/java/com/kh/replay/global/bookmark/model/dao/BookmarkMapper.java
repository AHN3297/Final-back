package com.kh.replay.global.bookmark.model.dao;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookmarkMapper {

    int checkBookmark(Map<String, Object> params);

    int insertBookmark(Map<String, Object> params);

    int deleteBookmark(Map<String, Object> params);

    int countBookmark(Long universeId);

}