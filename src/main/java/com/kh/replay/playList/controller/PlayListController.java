package com.kh.replay.playList.controller;



import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.replay.global.api.model.dto.MusicDTO;
import com.kh.replay.global.common.ResponseData;
import com.kh.replay.member.model.vo.CustomUserDetails;
import com.kh.replay.playList.model.dto.PlayListDTO;
import com.kh.replay.playList.model.dto.UpdateOrderDTO;
import com.kh.replay.playList.model.service.PlayListService;
import com.kh.replay.playList.model.vo.PlayListTrackVO;
import com.sun.net.httpserver.Authenticator.Result;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/member/playList")
@RequiredArgsConstructor
public class PlayListController {
	private final PlayListService playListService;

	// 플레이리스트 추가
	@PostMapping
    public ResponseEntity<ResponseData<Integer>> createPlayList(
            @RequestBody PlayListDTO playListDto,
            //@SessionAttribute(name="loginUser") String memberId) {
            @AuthenticationPrincipal CustomUserDetails user){
        
        // 서비스에서 실패 시 예외를 던지므로 성공 데이터(ID)만 받으면 됨
        int playListId = playListService.createPlayList(playListDto, user.getUsername());
        
        // ResponseData의 created 정적 메서드 활용
        return ResponseData.created(playListId, "플레이리스트 생성 성공");
    }
	
	// 플레이 리스트 목록 조회
	@GetMapping
	public ResponseEntity<ResponseData<List<PlayListDTO>>> findAllPlaylist(
			@AuthenticationPrincipal CustomUserDetails user) {
	    List<PlayListDTO> list = playListService.findAllMemberPlayLists(user.getUsername());
	    return ResponseData.ok(list, "내 플레이리스트 목록 조회 성공");
	}
	 
	// 메인 플레이리스트 지정
	@PostMapping("/main/{playListId}")
	public ResponseEntity<ResponseData<Integer>> mainPlayList(
			@AuthenticationPrincipal CustomUserDetails user,
	        @PathVariable(name="playListId") int playListId) {	    
	    int result = playListService.updateMainPlayList(user.getUsername(), playListId);
	    return ResponseData.ok(result, "메인 플레이리스트로 지정되었습니다.");
	}
	
	// 플레이리스트 이름 변경(수정)  
	@PatchMapping("/{playListId}")
	public ResponseEntity<ResponseData<Integer>> updatePlayListName(
	    @PathVariable(name = "playListId") int playListId, 
	    @AuthenticationPrincipal CustomUserDetails user, // 후에 수정해야함
	    @RequestBody PlayListDTO playListDto) {
	    
	    int result = playListService.updatePlayListName(playListId, user.getUsername(), playListDto.getPlayListName());  
	    return ResponseData.ok(result, "플레이리스트 이름 수정 성공");   
	}
	// 플레이리스트 삭제
	@DeleteMapping("/{playListId}")
	public ResponseEntity<ResponseData<Integer>> deletePlayList(
	        @PathVariable(name = "playListId") int playListId,
	        @AuthenticationPrincipal CustomUserDetails user) {
	    
	    // 서비스 호출
	    int result = playListService.deletePlayList(playListId, user.getUsername());
	    
	    return ResponseData.ok(result, "플레이리스트가 성공적으로 삭제되었습니다.");
	}
	
	
	// 플레이리스트에 노래 추가
	@PostMapping("/{playListId}/tracks")
	public ResponseEntity<ResponseData<Integer>> createPlayListSong(
			@RequestBody MusicDTO musicDto,
			@PathVariable(name = "playListId") int playListId,
			@AuthenticationPrincipal CustomUserDetails user){
		
		
		int result = playListService.createPlayListSong(musicDto, playListId, user.getUsername());
		
		return ResponseData.created(result, "플레이리스트에 곡이 추가 되었습니다.");
	}
	
	// 플레이리스트 상세 조회(노래 조회)
	// PlayListController.java
	@GetMapping("/{playListId}")
	public ResponseEntity<ResponseData<List<PlayListTrackVO>>> getPlaylistTracks(
	        @PathVariable(name = "playListId") int playListId,
	        @AuthenticationPrincipal CustomUserDetails user) {
	    
	    List<PlayListTrackVO> list = playListService.getPlaylistTracks(playListId, user.getUsername());
	    return ResponseData.ok(list, "플레이리스트 곡 목록 조회 성공");
	}
	
	// 플레이리스트 노래 순서 변경
	@PatchMapping("/{playListId}/tracks/order")
	public ResponseEntity<ResponseData<Integer>> updateTrackOrder(
	        @PathVariable(name = "playListId") int playListId,
	        @RequestBody List<UpdateOrderDTO> orderList,
	        @AuthenticationPrincipal CustomUserDetails user) {
	    
	    int result = playListService.updateTrackOrder(playListId, user.getUsername(), orderList);
	    return ResponseData.ok(result, "재생 순서가 변경되었습니다.");
	}
	
	// 플레이리스트 노래 삭제
	@DeleteMapping("/{playListId}/tracks/{songId}")
	public ResponseEntity<ResponseData<Integer>> deletePlayListRracks(
			@PathVariable(name="playListId") int playListId,
			@PathVariable(name="songId") int songId,
			@AuthenticationPrincipal CustomUserDetails user){
			
		int result = playListService.deletePlaylistTracks(playListId, songId, user.getUsername());
		return ResponseData.ok(result, "삭제가 완료되었습니다.");
		
	};
	
	

}
