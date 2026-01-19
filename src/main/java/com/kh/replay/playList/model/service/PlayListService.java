package com.kh.replay.playList.model.service;

import com.kh.replay.playList.model.dto.PlayListDTO;

public interface PlayListService {

	PlayListDTO createPlayList(PlayListDTO playListDto, String memberId);

}
