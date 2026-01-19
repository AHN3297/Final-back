package com.kh.replay.global.api.itunes;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItunesVO {
	private int resultCount; // count가 0이면 예외처리
	private List<ItunesItem> results;
	
	@Getter
	@NoArgsConstructor
	public static class ItunesItem{
		private String artistName;
        private String collectionName; // 앨범명
        private String trackName;      // 곡명
        private String artworkUrl100;  // 앨범 커버
        private long trackId;
        private long artistId;
        private String previewUrl;     // 미리듣기
        private String primaryGenreName;
        private String releaseDate;	
	}

}
