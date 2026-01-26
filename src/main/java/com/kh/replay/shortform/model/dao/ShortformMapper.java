package com.kh.replay.shortform.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.replay.shortform.model.dto.ShortformDTO;

@Mapper
public interface ShortformMapper {

	List<ShortformDTO> findAllShortform(Map<String, Object> shortformSearch);

	List<ShortformDTO> findByKeyword(Map<String, Object> shortformSearch);

	ShortformDTO findByShortFormId(Long shortFormId);

	int insertShortform(ShortformDTO shortform);

	int updateShortform(ShortformDTO shortform);

	int deleteShortform(Long shortFormId);

}