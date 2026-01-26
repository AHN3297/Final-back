package com.kh.replay.global.like.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeTrackVO {
    // 1. DB 시퀀스 번호 (TB_API_SONG.SONG_NO)
    private Long songNo;         
    
    // API 고유 ID (TB_API_SONG.TRACK_ID, ARTIST_ID)
    private Long trackId;      
    private Long artistId;     
    
    private String title;
    private String artistName;
    private String album;
    private String genreName;
    private String duration;     
    private String releaseDate;
    private String coverImgUrl;
    private String previewUrl;
    private int trackOrder;
}