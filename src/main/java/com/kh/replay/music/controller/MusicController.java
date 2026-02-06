package com.kh.replay.music.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.api.model.dto.MusicDTO;
import com.kh.replay.global.common.ResponseData;
import com.kh.replay.music.model.service.MusicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @GetMapping("/search")
    public ResponseEntity<ResponseData<Object>> search(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "category") String category, // "song", "artist", "genre"
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false) String sort) {

        // 서비스에서 가공된 데이터(MusicDTO 또는 ArtistDTO 리스트)를 가져옴
        Object result = musicService.searchByKeyword(keyword, category, page, size, sort);
        
        return ResponseData.ok(result, "검색 조회가 완료되었습니다.");
    }
    
    // 노래 상세 조회
    @GetMapping("/music/{trackId}")
    public ResponseEntity<ResponseData<MusicDTO>> musicDetail(@PathVariable(value = "trackId") Long trackId) {
        // Service에서 lyricsClient 호출 로직을 제거한 버전을 사용하세요.
        MusicDTO result = musicService.musicDetail(trackId); 
        return ResponseData.ok(result, "노래 상세 정보 조회 성공");
    }

    // 가사 정보만 따로 조회 (외부 API 호출/크롤링용)
    @GetMapping("/music/{trackId}/lyrics")
    public ResponseEntity<ResponseData<String>> musicLyrics(@PathVariable (value = "trackId") Long trackId) {
        // @RequestParam artistName, title 이 있다면 무조건 삭제하세요!
        // 그래야 프론트엔드에서 파라미터 없이 호출해도 400 에러가 안 납니다.
        String lyrics = musicService.getLyricsByTrackId(trackId);
        return ResponseData.ok(lyrics, "가사 조회 성공");
    }

    // 가수 상세 조회
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ResponseData<ArtistDTO>> artistDetail(@PathVariable(value = "artistId") Long artistId) {
        ArtistDTO result = musicService.artistDetail(artistId);
        return ResponseData.ok(result, "가수 상세 정보 조회 성공");
    }
    
    // 최신 노래 목록
    @GetMapping("/music/new")
    public ResponseEntity<ResponseData<List<MusicDTO>>> getNewMusic() {
        List<MusicDTO> newMusic = musicService.getNewMusic();
        return ResponseData.ok(newMusic, "최신 노래 조회 성공");
    }
    
    @GetMapping("/music/top-music")
    public ResponseEntity<ResponseData<List<MusicDTO>>> getTopMusic(){
    	List<MusicDTO> top = musicService.getTopMusic();
    	return ResponseData.ok(top,"인기 노래 조회 성공");
    }
    
}


