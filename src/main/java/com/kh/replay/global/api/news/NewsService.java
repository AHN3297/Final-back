package com.kh.replay.global.api.news;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class NewsService {

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    private final String[] RANDOM_IMAGES = {
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500",
        "https://images.unsplash.com/photo-1514320291840-2e0a9bf2a9ae?w=500",
        "https://images.unsplash.com/photo-1493225255756-d9584f8606e9?w=500"
    };

    // 카테고리별 검색어 매핑
    private static final Map<String, String> CATEGORY_KEYWORDS = Map.of(
        "연예", "연예 뉴스 최신",
        "K-POP", "케이팝 아이돌 뉴스",
        "음악", "음악 최신 뉴스",
        "인터뷰", "가수 인터뷰"
    );

    public List<NewsDTO> getMagazineData(String category) {
        String searchQuery = CATEGORY_KEYWORDS.getOrDefault(category, "음악 최신");

        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com/v1/search/news.json")
                .queryParam("query", searchQuery)
                .queryParam("display", 10)
                .queryParam("start", 1)
                .queryParam("sort", "date")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        RequestEntity<Void> req = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        RestTemplate restTemplate = new RestTemplate();
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
