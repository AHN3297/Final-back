package com.kh.replay.playList.controller;



import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.playList.model.service.PlayListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/member/playList")
@RequiredArgsConstructor
public class PlayListController {
	private PlayListService playListService;
	private PlayListDTO playListDTO;

	// 플레이리스트 추가
	@PostMapping
    public ResponseEntity<Map<String, Object>> addPlayList(
            @RequestBody PlayListDTO playListDto,
            @SessionAttribute(name="loginUser") String memberId) {
        
        PlayListDTO result = playListService.createPlayList(playListDto, memberId);  
        
        Map<String, Object> response = new HashMap<>();
        if (result != null) {
            response.put("status", 201);
            response.put("message", "플레이리스트 생성 성공");
            response.put("data", result);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("status", 400);
            response.put("message", "플레이리스트 생성 실패");
            return ResponseEntity.badRequest().body(response);
        }
    }
	
	// 플레이 리스트 목록 조회
	
	// 플레이리스트 상세조회
	
	// 플레이리스트에 노래 추가
	
	// 플레이리스트 노래 조회
}
