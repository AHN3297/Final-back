package com.kh.replay.playList.model.service;

import java.util.List;

import com.kh.replay.global.api.model.dto.MusicDTO;
import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.playList.model.dto.UpdateOrderDTO;
import com.kh.replay.playList.model.vo.PlayListTrackVO;


public interface PlayListService {

	int createPlayList(PlayListDTO playListDto, String memberId);

	List<PlayListDTO> findAllMemberPlayLists(String memberId);

	int updateMainPlayList(String memberId, int playlistId);

	int updatePlayListName(int playListId, String playListName, String newName);

	int deletePlayList(int playListId, String memberId);

	int createPlayListSong(MusicDTO musicDto, int playListId, String memberId);

	List<PlayListTrackVO> getPlaylistTracks(int playListId, String memberId);

	int updateTrackOrder(int playListId, String memberId, List<UpdateOrderDTO> orderList);

	int deletePlaylistTracks(int playListId, int songId, String memberId);


}
