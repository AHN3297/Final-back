package com.kh.replay.universe.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.replay.universe.model.dto.UniverseDTO;

@Mapper
public interface UniverseMapper {

	
    List<UniverseDTO> findAllUniverse(
        @Param("sort") String sort,
        @Param("lastUniverseId") Long lastUniverseId,
        @Param("lastLikeCount") Long lastLikeCount,
        @Param("limit") int limit
    );

	List<UniverseDTO> findByKeyword(
		@Param("keyword") String keyword, 
		@Param("condition")	String condition, 
		@Param("sort")	String sort, 
		@Param("lastUniverseId")	Long lastUniverseId,
		@Param("lastLikeCount")	Long lastLikeCount, 
		@Param("limit")	int limit);

	UniverseDTO findByUniverseId( 
			@Param("universeId") Long universeId);

	int insertUniverse(UniverseDTO universe);
}