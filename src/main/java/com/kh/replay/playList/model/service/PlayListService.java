package com.kh.replay.playList.model.service;

import java.util.List;

import com.kh.replay.playList.model.dto.PlayListDTO;


public interface PlayListService {

	int createPlayList(PlayListDTO playListDto, String memberId);

	List<PlayListDTO> findAllMemberPlayLists(String memberId);

}
