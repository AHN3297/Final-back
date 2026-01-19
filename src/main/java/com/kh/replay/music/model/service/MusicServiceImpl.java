package com.kh.replay.music.model.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kh.replay.global.api.deezer.DeezerClient;
import com.kh.replay.global.api.deezer.DeezerVO;
import com.kh.replay.global.api.itunes.ItunesClient;
import com.kh.replay.global.api.itunes.ItunesVO;
import com.kh.replay.global.api.lyricsOvh.LyricsClient;
import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.api.model.dto.MusicDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private final ItunesClient itunesClient;
    private final DeezerClient deezerClient;
    private final LyricsClient lyricsClient;

    @Override
    public Object searchByKeyword(String keyword, String category, int page, int size, String sort) {
        int limit = size;

        if ("artist".equals(category)) {
            ItunesVO rawData = itunesClient.findAllSearch(keyword, "musicArtist", limit);
            return convertToArtistDtoList(rawData);
        } else {
            ItunesVO rawData = itunesClient.findAllSearch(keyword, "song", limit);
            return convertToMusicDtoList(rawData);
        }
    }
    // 노래 목록
    private List<MusicDTO> convertToMusicDtoList(ItunesVO rawData) {
        if (rawData == null || rawData.getResults() == null) return Collections.emptyList();

        return rawData.getResults().stream().map(item -> 
            MusicDTO.builder()
                .trackId(item.getTrackId())     // Long 타입 식별자
                .artistId(item.getArtistId())   // Long 타입 식별자
                .title(item.getTrackName())
                .artistName(item.getArtistName())
                .album(item.getCollectionName())
                .coverImgUrl(item.getArtworkUrl100())
                .previewUrl(item.getPreviewUrl())
                .releaseDate(item.getReleaseDate())
                .genreName(item.getPrimaryGenreName())
                .build()
        ).collect(Collectors.toList());
    }

    // 가수 목록 
    private List<ArtistDTO> convertToArtistDtoList(ItunesVO rawData) {
        if (rawData == null || rawData.getResults() == null) return Collections.emptyList();

        return rawData.getResults().stream().map(item -> {
            ArtistDTO dto = ArtistDTO.builder()
                .apiSingerId(item.getArtistId())
                .apiSingerName(item.getArtistName())
                .singerGenre(item.getPrimaryGenreName())
                .build();

            // Deezer에서 고화질 아티스트 이미지 추가 주입
            try {
                DeezerVO deezerData = deezerClient.fetchArtist(item.getArtistName());
                if (deezerData != null && !deezerData.getData().isEmpty()) {
                    dto.setSingerImgUrl(deezerData.getData().get(0).getPicture_xl());
                }
            } catch (Exception e) {
                // 이미지 호출 실패 시 null 유지
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public MusicDTO musicDetail(Long trackId) {
        // 1. iTunes 데이터 조회
        ItunesVO rawData = itunesClient.findById(String.valueOf(trackId));
        if (rawData == null || rawData.getResults().isEmpty()) return null;

        ItunesVO.ItunesItem item = rawData.getResults().get(0);

        // 2. 가사 조회 (LyricsClient 호출)
        String lyrics = lyricsClient.lyrics(item.getArtistName(), item.getTrackName());

        // 3. DTO 조립
        return MusicDTO.builder()
                .trackId(item.getTrackId())
                .artistId(item.getArtistId())
                .title(item.getTrackName())
                .artistName(item.getArtistName())
                .album(item.getCollectionName())
                .coverImgUrl(item.getArtworkUrl100())
                .previewUrl(item.getPreviewUrl())
                .releaseDate(item.getReleaseDate())
                .genreName(item.getPrimaryGenreName())
                .lyrics(lyrics) // 가사 주입
                .build();
    }

	@Override
	public ArtistDTO artistDetail(Long artistId) {
		// TODO Auto-generated method stub
		return null;
	}
}