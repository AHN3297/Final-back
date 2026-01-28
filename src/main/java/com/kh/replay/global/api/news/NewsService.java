package com.kh.replay.global.api.news;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class NewsService {

    // 네이버 개발자 센터에서 발급받은 키 (application.properties 관리 권장)
    private final String CLIENT_ID = "7ApwBwhbNclMTWT6sQTt";
    private final String CLIENT_SECRET = "j3Z3IFlpRU";

    // 랜덤으로 보여줄 이미지 리스트 (무료 이미지 사이트 등에서 가져온 URL)
    private final String[] RANDOM_IMAGES = {
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500", // 음악 관련 이미지 1
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500", // 음악 관련 이미지 2
        "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=500", // 음악 관련 이미지 3
        "https://images.unsplash.com/photo-1493225255756-d9584f8606e9?w=500"  // 음악 관련 이미지 4
    };

    public List<NewsDTO> getMagazineData() {
        // 1. 요청 URL 설정 (검색어: 음악, 뮤직, 콘서트 등)
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/news.json")
                .queryParam("query", "음악 최신")
                .queryParam("display", 10)
                .queryParam("start", 1)
                .queryParam("sort", "sim") // sim: 정확도순, date: 날짜순
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 2. 헤더에 ID, Secret 추가
        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", CLIENT_ID)
                .header("X-Naver-Client-Secret", CLIENT_SECRET)
                .build();

        // 3. API 호출
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<NaverNewsResponse> response = restTemplate.exchange(req, NaverNewsResponse.class);

        // 4. 데이터 가공 (API 응답 -> NewsDTO)
        List<NewsDTO> newsList = new ArrayList<>();
        if (response.getBody() != null && response.getBody().getItems() != null) {
            Random random = new Random();
            
            for (NaverNewsResponse.NaverNewsItem item : response.getBody().getItems()) {
                
                // HTML 태그 제거 (제목/내용에 <b> 등이 포함되어 옴)
                String cleanTitle = item.getTitle().replaceAll("<[^>]*>", "").replaceAll("&quot;", "\"");
                String cleanDesc = item.getDescription().replaceAll("<[^>]*>", "").replaceAll("&quot;", "\"");

                NewsDTO dto = NewsDTO.builder()
                        .title(cleanTitle)
                        .description(cleanDesc)
                        .url(item.getLink())
                        .publishedAt(item.getPubDate())
                        .author("Naver Search")
                        // 랜덤 이미지 배정
                        .imageUrl(RANDOM_IMAGES[random.nextInt(RANDOM_IMAGES.length)]) 
                        .build();

                newsList.add(dto);
            }
        }

        return newsList;
    }
}