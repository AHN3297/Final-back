package com.kh.replay.music.model.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
                .trackId(item.getTrackId())     
                .artistId(item.getArtistId())   
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
                .lyrics(lyrics)
                .build();
    }
    @Override
    public ArtistDTO artistDetail(Long artistId) {
        // 1. Client를 통해 데이터 수신 (RestTemplate은 Client 안에서만 돔)
        ItunesVO response = itunesClient.findArtistWithSongs(artistId, 11);

        if (response == null || response.getResults().isEmpty()) return null;

        // 2. 데이터 가공 (첫 번째: 아티스트, 나머지: 노래)
        ItunesVO.ItunesItem artistItem = response.getResults().get(0);

        List<MusicDTO> topSongs = response.getResults().stream()
                .skip(1)
                .map(item -> MusicDTO.builder()
                        .trackId(item.getTrackId())
                        .artistId(item.getArtistId())
                        .title(item.getTrackName())
                        .artistName(item.getArtistName())
                        .album(item.getCollectionName())
                        .genreName(item.getPrimaryGenreName())
                        .coverImgUrl(item.getArtworkUrl100())
                        .previewUrl(item.getPreviewUrl())
                        .build())
                .collect(Collectors.toList());

        // 3. DTO 조립
        ArtistDTO dto = ArtistDTO.builder()
                .apiSingerId(artistItem.getArtistId())
                .apiSingerName(artistItem.getArtistName())
                .singerGenre(artistItem.getPrimaryGenreName())
                .topSongs(topSongs)
                .build();

        // 4. Deezer 데이터 결합
        try {
            DeezerVO deezerData = deezerClient.fetchArtist(artistItem.getArtistName());
            if (deezerData != null && !deezerData.getData().isEmpty()) {
                dto.setSingerImgUrl(deezerData.getData().get(0).getPicture_xl());
                dto.setDescription(artistItem.getArtistName() + "은(는) " + 
                                   artistItem.getPrimaryGenreName() + " 장르의 인기 아티스트입니다.");
            }
        } catch (Exception e) {
            dto.setDescription("상세 정보를 불러올 수 없습니다.");
        }

        return dto;
    }
    

}