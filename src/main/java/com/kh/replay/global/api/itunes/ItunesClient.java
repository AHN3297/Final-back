package com.kh.replay.global.api.itunes;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ItunesClient {

    private final RestTemplate restTemplate;

    @Value("${api.itunes.url}")
    private String itunesUrl;

    /**
     * 기본 검색 API 호출
     * entity => song, artist인 카테고리
     * limit => page, size인 페이징
     */
    public ItunesVO findAllSearch(String keyword, String entity, int limit) {
        String url = String.format("%s?term=%s&entity=%s&limit=%d&country=KR", 
                                    itunesUrl, keyword, entity, limit);
        return restTemplate.getForObject(url, ItunesVO.class);
    }

    /**
     * 인기 차트 API 호출 (Top Songs)
     */
    public Map<String, Object> findAllTopCharts(int limit) {
        String url = "https://itunes.apple.com/kr/rss/topsongs/limit=" + limit + "/json";
        // Map으로 받고 Service에서 파싱
        return restTemplate.getForObject(url, Map.class);
    }
    
    /**
     * ID 기반 단건 조회 (상세보기용)
     */
    public ItunesVO findById(String id) {
        String url = String.format("https://itunes.apple.com/lookup?id=%s&country=KR", id);
        return restTemplate.getForObject(url, ItunesVO.class);
    }
    
    /**
     * 가수 상세보기 관련 노래
     */
    public ItunesVO findArtistWithSongs(Long artistId, int limit) {
        // lookup 주소를 사용하며, 가수의 노래 목록을 함께 요청
        String url = String.format("https://itunes.apple.com/lookup?id=%d&entity=song&limit=%d&country=KR", 
                                    artistId, limit);
        return restTemplate.getForObject(url, ItunesVO.class);
    }
}