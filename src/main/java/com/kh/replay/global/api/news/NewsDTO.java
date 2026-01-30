package com.kh.replay.global.api.news;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsDTO {
	private String title;      
    private String description;
    private String url;        
    private String imageUrl;  
    private String publishedAt;
    private String author;    
}