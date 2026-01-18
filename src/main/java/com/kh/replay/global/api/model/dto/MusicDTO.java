package com.kh.replay.global.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicDTO {
    private Long trackId;       // TRACK_ID 
    private Long artistId;      // ARTIST_ID 
    private String title;       // TITLE
    private String artistName;  // ARTIST_NAME
    private String album;       // ALBUM
    private String genreName;   // GENRE_NAME
    private String coverImgUrl; // COVERIMG_URL
    private String previewUrl;  // PREVIEW_URL
    private String releaseDate; // RELEASE_DATE
    private String duration;    // DURATION
}
