package com.kh.replay.global.api.lyricsOvh;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LyricsClient {
    private final RestTemplate restTemplate;
    
    @Value("${api.lyrics.url}")
    private String lyricsUrl;

    public String lyrics(String artist, String title) {
        try {
            String url = String.format("%s/%s/%s", lyricsUrl, artist, title);
            Map<String, String> response = restTemplate.getForObject(url, Map.class);
            return (response != null) ? response.get("lyrics") : null;
        } catch (Exception e) {
            
            return "가사를 찾을 수 없습니다.";
        }
    }
}