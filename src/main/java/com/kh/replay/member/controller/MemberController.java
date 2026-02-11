package com.kh.replay.member.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.global.common.ResponseData;
import com.kh.replay.member.model.dto.ChangePasswordDTO;
import com.kh.replay.member.model.dto.GenreDTO;
import com.kh.replay.member.model.dto.MemberInfoDTO;
import com.kh.replay.member.model.dto.MemberUpdateRequest;
import com.kh.replay.member.model.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/members")
@Slf4j
@RequiredArgsConstructor
public class MemberController {
	
	private final MemberService memberService;
	
	@PostMapping("/login")
	public ResponseEntity<ResponseData<Map<String, String>>> memberlogin(@RequestBody LocalDTO local) {
		Map<String, String> loginResponse = memberService.memberLogin(local);
		
		return ResponseData.ok(loginResponse, "로그인에 성공하셨습니다.");
	}
	
	@PostMapping("/logout")
	public ResponseEntity<ResponseData<String>> memberlogout(@Valid @RequestBody LocalDTO local) {
		memberService.memberLogout(local);
		
		return ResponseData.ok(null, "로그아웃 되었습니다.");
	}
	
	@PutMapping
	public ResponseEntity<ResponseData<ChangePasswordDTO>> changePassword(@RequestBody ChangePasswordDTO password) {
		memberService.changePassword(password);
		
		return ResponseData.created(password, "비밀번호가 변경되었습니다.");
	}
	
	@GetMapping
	public ResponseEntity<ResponseData<MemberInfoDTO>> findAllInfo(
			@RequestParam(name = "memberId") String memberId) {
		log.info("회원아이디 조회{}", memberId);
		MemberInfoDTO memberInfo = memberService.findAllInfo(memberId);
		
		return ResponseData.ok(memberInfo, "조회에 성공하셨습니다.");
	}
	
	@PatchMapping
	public ResponseEntity<ResponseData<List<MemberInfoDTO>>> changeInfo(@RequestBody MemberUpdateRequest request) {
		List<MemberInfoDTO> result = memberService.changeInfo(request);
		
		return ResponseData.ok(result, "회원정보 수정에 성공하셨습니다.");
	}
	
	@DeleteMapping
	public ResponseEntity<ResponseData<String>> withdrawMember(@RequestBody LocalDTO local) {
		memberService.withdrawMember(local);
		
		return ResponseData.ok(null, "삭제에 성공하셨습니다.");
	}
	
	@GetMapping("/genres")
	public ResponseEntity<ResponseData<List<GenreDTO>>> findAllGenres() {
		List<GenreDTO> genres = memberService.findAllGenres();
		return ResponseData.ok(genres, "장르 전체 조회 성공");
	}
}