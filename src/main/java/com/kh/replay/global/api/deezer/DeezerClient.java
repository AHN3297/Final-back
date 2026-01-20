package com.kh.replay.global.api.deezer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class DeezerClient {

    private final RestTemplate restTemplate;

    @Value("${api.deezer.url}") // https://api.deezer.com
    private String deezerUrl;

    public DeezerVO fetchArtist(String artistName) {
        String url = String.format("%s/search/artist?q=%s", deezerUrl, artistName);
        return restTemplate.getForObject(url, DeezerVO.class);
    }
}