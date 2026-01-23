package com.kh.replay.global.like.LikeController;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.global.common.ResponseData;
import com.kh.replay.global.like.model.dto.LikeResponse;
import com.kh.replay.global.like.model.service.GenreLikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite")
public class LikeController {
	
	private final GenreLikeService genreLikeService;
	
	@PostMapping("/genre")
	public ResponseEntity<ResponseData<LikeResponse>> likeGenre(
			@AuthenticationPrincipal UserDetails authenticatedUser,
			@RequestBody Map<String, String> request
			){
		LikeResponse result = genreLikeService.likeGenre(
		        authenticatedUser.getUsername(),
		        request.get("genreName")
		    );
		return ResponseData.created(result, "선호하는 장르가 추가되었습니다.");
	}

}
