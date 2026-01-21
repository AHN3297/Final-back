package com.kh.replay.playList.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.playList.model.vo.PlayListVO;

@Mapper
public interface PlayListMapper {
	
	// 플레이리스트 생성
	int createPlayList(PlayListVO plyList);
	
	// 플레이리스트 목록 조회
	List<PlayListDTO> findAllMemberPlayLists(String memberId);
	
	// 플레이리스트 상세조회
	PlayListVO selectPlayListById(int playListId);


	int deleteMainPlayList(String memberId);

	int createMainPlayList(@Param("memberId") String memberId, @Param("playListId") int playListId);

	int updatePlayList(PlayListDTO updateDto);
	
	


}
