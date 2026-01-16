package com.kh.replay.global.api.model.vo;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor // Jackson(JSON 변환기)을 위해 최소한의 생성자는 필요
public class ItemWrapper {
    private String artistName;
    private String artworkUrl100;
    private int trackId;
    private int artistId;
    private String trackName;
    private String primaryGenreName;
    private String previewUrl;
    private String releaseDate;
}