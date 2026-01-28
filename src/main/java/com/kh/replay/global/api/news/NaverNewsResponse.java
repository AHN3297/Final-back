package com.kh.replay.global.api.news;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
class NaverNewsResponse {
    private List<NaverNewsItem> items;

    @Getter @Setter
    public static class NaverNewsItem {
        private String title;
        private String originallink;
        private String link;
        private String description;
        private String pubDate;
    }
}