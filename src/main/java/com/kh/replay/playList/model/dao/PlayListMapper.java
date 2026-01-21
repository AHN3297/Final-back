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

	// 메인플레이리스트 삭제
	int deleteMainPlayList(String memberId);
	
	// 메인플레이리스트 추가 
	int createMainPlayList(@Param("memberId") String memberId, @Param("playListId") int playListId);

	// 플레이리스트 이름 변경
	int updatePlayListName(PlayListDTO updateDto);
	
	


}
