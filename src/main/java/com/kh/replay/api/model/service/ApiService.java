package com.kh.replay.api.model.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.kh.replay.api.model.dao.ApiDAO;
import com.kh.replay.api.model.dto.ApiResponseDTO;
import com.kh.replay.api.model.vo.ApiResponseVO;
import com.kh.replay.api.model.vo.ItemWrapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiService {

    @Value("${api.itunes.url}")
    private String itunesUrl;

    @Value("${api.deezer.url}")
    private String deezerUrl;

    private final RestTemplate restTemplate;
    // 나중에 DB 저장 시 사용
    private final ApiDAO apiDao; 

    /**
     * 노래 / 아티스트 검색 및 데이터 가공
     */
    public Map<String, Object> findAllByKeyword(String keyword, String category, int page, int size) {
        
        // 1. iTunes API
        String entity = category.equals("song") ? "song" : "musicArtist";
        String url = String.format("%s?term=%s&entity=%s&limit=%d&country=KR", itunesUrl, keyword, entity, size);
        
        // RestTemplate이 JSON 데이터를 VO에 자동 저장
        ApiResponseVO itunesResponse = restTemplate.getForObject(url, ApiResponseVO.class);
        List<ItemWrapper> itunesResults = itunesResponse != null ? itunesResponse.getResults() : new ArrayList<>();

        // 2. Deezer API 이미지 매칭 및 DTO 가공
        List<ApiResponseDTO> content = itunesResults.parallelStream()
        	    .<ApiResponseDTO>map(item -> { // <ApiResponseDTO>를 명시하여 추론 도움
        	        String artistName = item.getArtistName();
        	        String deezerSearchUrl = deezerUrl + "/search/artist?q=" + artistName;
        	        
        	        String mainImg = "";
        	        try {
        	            // Deezer API 호출
        	            Map<String, Object> deezerRes = restTemplate.getForObject(deezerSearchUrl, Map.class);
        	            List<Map<String, Object>> dData = (List<Map<String, Object>>) deezerRes.get("data");
        	            
        	            if (dData != null && !dData.isEmpty()) {
        	                Map<String, Object> dArtist = dData.get(0);
        	                mainImg = "artist".equals(category) ? 
        	                          (String) dArtist.get("picture_big") : 
        	                          item.getArtworkUrl100().replace("100x100bb", "600x600bb");
        	            }
        	        } catch (Exception e) {
        	            mainImg = item.getArtworkUrl100() != null ? 
        	                      item.getArtworkUrl100().replace("100x100bb", "600x600bb") : "";
        	        }

        	        // 정확한 DTO 클래스명(ApiResponseDTO) 사용 확인
        	        return ApiResponseDTO.builder()
        	            .id(item.getTrackId() != 0 ? String.valueOf(item.getTrackId()) : String.valueOf(item.getArtistId()))
        	            .category(category)
        	            .title(item.getTrackName())
        	            .artistName(artistName)
        	            .genre(item.getPrimaryGenreName())
        	            .mainImg(mainImg)
        	            .previewUrl(item.getPreviewUrl())
        	            .releaseDate(item.getReleaseDate() != null ? ((String) item.getReleaseDate()).split("T")[0] : "날짜 정보 없음")
        	            .build();
        	    }).collect(Collectors.toList());

        // 3. 최종 응답 구조 생성 (성공 응답 명세서 준수)
        Map<String, Object> finalResult = new HashMap<>();
        finalResult.put("content", content);
        finalResult.put("page", page);
        finalResult.put("size", size);
        finalResult.put("totalElements", itunesResults.size());
        finalResult.put("totalPages", (int) Math.ceil((double) itunesResults.size() / size));

        return finalResult;
    }
}
