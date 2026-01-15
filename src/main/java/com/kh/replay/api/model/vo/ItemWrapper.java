package com.kh.replay.api.model.vo;

import lombok.Data;

@Data // Getter, Setter를 자동으로 생성하여 API 데이터를 받아옵니다.
public class ItemWrapper {
    private String artistName;
    private String artworkUrl100;
    private int trackId;
    private int artistId; // long이나 int로 설정
    private String trackName;
    private String primaryGenreName;
    private String previewUrl;
    private String releaseDate; // JSON의 "2024-03-21T07:00:00Z" 형태를 문자열로 받음
}