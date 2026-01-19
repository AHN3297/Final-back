package com.kh.replay.global.api.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDTO {
    private Long apiSingerId;     		// API_SINGER_ID 
    private String apiSingerName; 		// API_SINGER_NAME
    private String singerGenre;   		// SINGER_GENRE
    private String singerImgUrl;  		// SINGER_IMG_URL
    private List<MusicDTO> topSongs; 	// 아티스트의 인기 노래 리스트
    private String description; 		// 가수 상세 소개글
}