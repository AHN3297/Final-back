package com.kh.replay.music.model.service;

import java.util.List;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.api.model.dto.MusicDTO;

public interface MusicService {

	Object searchByKeyword(String keyword, String category, int page, int size, String sort);
	
	// 노래 상세 조회
    MusicDTO musicDetail(Long trackId);
    
    // 가수 상세 조회
    ArtistDTO artistDetail(Long artistId);

	List<MusicDTO> getNewMusic();

	List<MusicDTO> getTopMusic();

	String getLyricsOnly(String artistName, String title);

	String getLyricsByTrackId(Long trackId);
}
