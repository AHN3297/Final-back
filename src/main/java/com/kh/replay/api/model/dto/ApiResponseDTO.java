package com.kh.replay.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 외부 API(iTunes, Deezer) 검색 결과를 
 * 공통 응답 규격에 맞게 가공하여 담는 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDTO {
    
    private String category;    // 검색 카테고리 (song, artist)
    private String id;          // 고유 식별 번호 (trackId 또는 artistId)
    private String title;       // 노래 제목 (가수 검색 시 빈 문자열)
    private String artistName;  // 가수 이름
    private String genre;       // 장르 이름
    private String mainImg;     // 메인 이미지 URL (고화질 변환 완료된 주소)
    private String previewUrl;  // 미리듣기 URL (M4A 주소)
    private String releaseDate; // 발매일 (YYYY-MM-DD 형식)
    
}