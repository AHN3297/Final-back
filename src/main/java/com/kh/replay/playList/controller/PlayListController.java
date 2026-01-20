package com.kh.replay.playList.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.kh.replay.global.common.ResponseData;
import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.playList.model.service.PlayListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/member/playList")
@RequiredArgsConstructor
public class PlayListController {
	private final PlayListService playListService;
	private PlayListDTO playListDTO;

	// 플레이리스트 추가
	@PostMapping
    public ResponseEntity<ResponseData<Integer>> createPlayList(
            @RequestBody PlayListDTO playListDto,
            //@SessionAttribute(name="loginUser") String memberId) {
            @RequestParam(name="memberId") String memberId){
        
        // 서비스에서 실패 시 예외를 던지므로 성공 데이터(ID)만 받으면 됨
        int playListId = playListService.createPlayList(playListDto, memberId);
        
        // ResponseData의 created 정적 메서드 활용
        return ResponseData.created(playListId, "플레이리스트 생성 성공");
    }
	
	// 플레이 리스트 목록 조회
	
	// 플레이리스트 상세조회
	
	// 플레이리스트에 노래 추가
	
	// 플레이리스트 노래 조회
}
