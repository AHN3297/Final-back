package com.kh.replay.playList.controller;



import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	@GetMapping
	public ResponseEntity<ResponseData<List<PlayListDTO>>> findAllPlaylist(
	        @RequestParam(name = "memberId") String memberId) {
		
	    List<PlayListDTO> list = playListService.findAllMemberPlayLists(memberId);
	    
	    return ResponseData.ok(list, "내 플레이리스트 목록 조회 성공");
	}
	

	 
	// 메인 플레이리스트 지정
	@PostMapping("/main")
	public ResponseEntity<ResponseData<Integer>> mainPlayList(
	        @RequestParam(name="memberId") String memberId,
	        @RequestParam(name="playListId") int playListId) {
	    
	    int result = playListService.updateMainPlayList(memberId, playListId);
	    
	    return ResponseData.ok(result, "메인 플레이리스트로 지정되었습니다.");
	}
	
	// 플레이리스트 이름 변경(수정)
	@PatchMapping("/{playListId}")
	public ResponseEntity<ResponseData<Integer>> updatePlayListName(
	    @PathVariable int playListId, 
	    @RequestBody PlayListDTO playListDto) {
		
		int result = playListService.updatePlayListName(playListId, playListDto.getPlayListName());
		
		return ResponseData.ok(1, "플레이리스트 이름 수정 성공");   
	}
	
	// 플레이리스트 삭제
	
	// 플레이리스트에 노래 추가
	
	// 플레이리스트 상세 조회(노래 조회)
	
	// 플레이리스트 노래 순서 변경
	
	// 플레이리스트 노래 삭제
	
	

}
