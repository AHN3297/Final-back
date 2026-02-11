package com.kh.replay.global.api.news;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public ResponseEntity<List<NewsDTO>> getNews(
            @RequestParam(value = "category", defaultValue = "음악") String category) {
        return ResponseEntity.ok(newsService.getMagazineData(category));
    }
}
