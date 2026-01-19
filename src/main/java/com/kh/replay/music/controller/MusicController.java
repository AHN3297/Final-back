package com.kh.replay.music.controller;

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
        MusicDTO result = musicService.musicDetail(trackId);
        return ResponseData.ok(result, "노래 상세 정보 조회 성공");
    }

    // 가수 상세 조회
    @GetMapping("/artist/{artistId}")
    public ResponseEntity<ResponseData<ArtistDTO>> artistDetail(@PathVariable Long artistId) {
        ArtistDTO result = musicService.artistDetail(artistId);
        return ResponseData.ok(result, "가수 상세 정보 조회 성공");
    }
    
}


