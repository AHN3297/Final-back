package com.kh.replay.universe.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import com.kh.replay.universe.model.dto.UniverseDTO;

@Mapper
public interface UniverseMapper {

    List<UniverseDTO> findAllUniverse(Map<String, Object> universeSearch);

    List<UniverseDTO> findByKeyword(Map<String, Object> universeSearch);

    UniverseDTO findByUniverseId(Long universeId);

    int insertUniverse(UniverseDTO universe);

    int updateUniverse(UniverseDTO update);

    int deleteUniverse(Long universeId);

}