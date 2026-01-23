package com.kh.replay.global.like.LikeController;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.global.api.model.dto.ArtistDTO;
import com.kh.replay.global.common.ResponseData;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.global.like.model.service.LikeService;
import com.kh.replay.member.model.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {
	
	private final LikeService likeService;

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
    
    // 좋아하는 가수 삭제
    @DeleteMapping("/artists/{singerNo}")
    public ResponseEntity<ResponseData<LikeResponse>> unlikeArtist(
            @PathVariable("singerNo") int singerNo,
            @AuthenticationPrincipal CustomUserDetails user) {
        
        LikeResponse response = likeService.unlikeArtist(singerNo, user.getUsername());
        return ResponseData.ok(response, "아티스트 좋아요 취소 성공");
    }

	
	
	// 좋아하는 노래 추가
	// 좋아하는 노래 조회
	// 좋아하는 노래 삭제
	
	
	// 좋아하는 장르 추가
	// 좋아하는 장르 조회
	// 좋아하는 장르 삭제

}
