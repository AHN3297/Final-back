package com.kh.replay.global.like.LikeController;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.api.model.dto.MusicDTO;
import com.kh.replay.global.common.ResponseData;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.global.like.model.service.GenreLikeService;
import com.kh.replay.global.like.model.service.LikeService;
import com.kh.replay.global.like.model.vo.LikeArtistVO;
import com.kh.replay.global.like.model.vo.LikeTrackVO;
import com.kh.replay.member.model.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class LikeController {
	
	private final LikeService likeService;
	private final GenreLikeService genreLikeService;

	// 좋아하는 가수 추가
    @PostMapping("/artists")
    public ResponseEntity<ResponseData<LikeResponse>> likeArtist(
            @RequestBody ArtistDTO artistDto,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        // 지휘자(LikeService)에게 흐름 제어 요청
        LikeResponse response = likeService.likeArtist(artistDto, user.getUsername());
        return ResponseData.ok(response, "아티스트 좋아요 성공");
    }
    
    // 좋아하는 가수 조회
    @GetMapping("/artists")
    public ResponseEntity<ResponseData<List<LikeArtistVO>>> getMyFavoriteArtists(
            @AuthenticationPrincipal CustomUserDetails user) {
        
        List<LikeArtistVO> list = likeService.findAllFavoriteArtists(user.getUsername());
        return ResponseData.ok(list, "좋아하는 가수 목록 조회 성공");
    }
    
    
    // 좋아하는 가수 삭제
    @DeleteMapping("/artists/{singerNo}")
    public ResponseEntity<ResponseData<LikeResponse>> unlikeArtist(
            @PathVariable("singerNo") int singerNo,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        LikeResponse response = likeService.unlikeArtist(singerNo, user.getUsername());
        return ResponseData.ok(response, "아티스트 좋아요 취소 성공");
    }

	
	
	// 좋아하는 노래 추가
    @PostMapping("/tracks")
    public ResponseEntity<ResponseData<LikeTrackVO>> likeTrack(
            @RequestBody MusicDTO musicDto,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        // JwtFilter를 통해 정제된 #101 등의 ID 사용
        LikeTrackVO result = likeService.likeTrack(musicDto, user.getUsername());
        
        return ResponseData.ok(result, "노래 좋아요가 완료되었습니다.");
    }
    // 조회
    @GetMapping("/tracks")
    public ResponseEntity<ResponseData<List<LikeTrackVO>>> getLikes(@AuthenticationPrincipal CustomUserDetails user) {
        List<LikeTrackVO> list = likeService.getMyLikes(user.getUsername());
        return ResponseData.ok(list, "조회 성공");
    }

    // 삭제
    @DeleteMapping("/tracks/{songNo}")
    public ResponseEntity<ResponseData<Integer>> deleteLike(
            @AuthenticationPrincipal CustomUserDetails user, 
            @PathVariable("songNo") Long songNo) {
        likeService.deleteLike(user.getUsername(), songNo);
        return ResponseData.ok(null, "삭제 성공");
    }

    // 순서 변경 (리스트 전체를 받아 업데이트)
    @PutMapping("/tracks/reorder")
    public ResponseEntity<ResponseData<Integer>> reorder(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody List<LikeTrackVO> orderList) {
        int result = likeService.reorderTracks(user.getUsername(), orderList);
        return ResponseData.ok(result, "노래 "+ result + "개 순서 변경 완료");
    }
	
	
	// 좋아하는 장르 추가
    @PostMapping("/genre")
	public ResponseEntity<ResponseData<LikeResponse>> likeGenre(
			@AuthenticationPrincipal CustomUserDetails authenticatedUser,
			@RequestBody Map<String, String> request
			){
		LikeResponse result = genreLikeService.likeGenre(
		        authenticatedUser.getUsername(),
		        request.get("genreName")
		    );
		return ResponseData.created(result, "선호하는 장르가 추가되었습니다.");
	}
	// 좋아하는 장르 조회
	// 좋아하는 장르 삭제


}
