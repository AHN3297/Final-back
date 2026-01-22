package com.kh.replay.playList.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.playList.model.dto.UpdateOrderDTO;
import com.kh.replay.playList.model.vo.PlayListTrackVO;
import com.kh.replay.playList.model.vo.PlayListVO;

@Mapper
public interface PlayListMapper {
	// 시퀀스 번호만 미리 가져오는 메서드
	long getNewSongNo();
	
	int checkPlayListOwnership(@Param("playListId") int playListId, @Param("memberId") String memberId);
	
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

	// 플레이리스트 삭제
	int deletePlayList(@Param("playListId") int playListId, @Param("memberId") String memberId);
	
	// 곡 담기
	int createTrack(PlayListTrackVO trackVo);

	int insertPlaylistSong(@Param("playListId")int playListId, @Param("trackId") Long trackId, @Param("nextOrder") int nextOrder);
	
	int getNextTrackOrder(int playListId);
	
	// 특정 플레이리스트에 담긴 곡 목록 조회
	List<PlayListTrackVO> selectPlaylistTracks(@Param("playListId") int playListId, @Param("memberId") String memberId);
	
	// 곡순서 조정
	int updateTrackOrder(@Param("item") UpdateOrderDTO dto);
	
	// 곡 삭제
	int deletePlaylistTracks(@Param("playListId") int playListId, @Param("songId") int songId,@Param("memberId") String memberId);
	
}
