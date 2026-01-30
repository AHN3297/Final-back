package com.kh.replay.global.api.news;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class NewsService {

    // [수정 1] @Value 문법 수정 ("${키이름}") 및 final 제거
    @Value("${naver.client.id}")
    private String clientId;
	
    // [수정 2] 시크릿 키도 properties에서 관리하도록 수정 (보안 권장)
    @Value("${naver.client.secret}")
    private String clientSecret;

    // 랜덤으로 보여줄 이미지 리스트
    private final String[] RANDOM_IMAGES = {
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500",
        "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=500",
        "https://images.unsplash.com/photo-1493225255756-d9584f8606e9?w=500"
    };

    public List<NewsDTO> getMagazineData() {
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/news.json")
                .queryParam("query", "음악 최신")
                .queryParam("display", 10)
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                // [수정 3] 위에서 주입받은 변수 사용 (대소문자 주의)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        // NaverNewsResponse 클래스가 정의되어 있어야 합니다.
        ResponseEntity<NaverNewsResponse> response = restTemplate.exchange(req, NaverNewsResponse.class);

        List<NewsDTO> newsList = new ArrayList<>();
        if (response.getBody() != null && response.getBody().getItems() != null) {
            Random random = new Random();
            
            for (NaverNewsResponse.NaverNewsItem item : response.getBody().getItems()) {
                
                String cleanTitle = item.getTitle().replaceAll("<[^>]*>", "").replaceAll("&quot;", "\"");
                String cleanDesc = item.getDescription().replaceAll("<[^>]*>", "").replaceAll("&quot;", "\"");

                NewsDTO dto = NewsDTO.builder()
                        .title(cleanTitle)
                        .description(cleanDesc)
                        .url(item.getLink())
                        .publishedAt(item.getPubDate())
                        .author("Naver Search")
                        .imageUrl(RANDOM_IMAGES[random.nextInt(RANDOM_IMAGES.length)]) 
                        .build();

                newsList.add(dto);
            }
        }

        return newsList;
    }
}