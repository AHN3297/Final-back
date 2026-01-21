package com.kh.replay.global.member.controller;


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
import com.kh.replay.global.member.model.dto.ChangePasswordDTO;
import com.kh.replay.global.member.model.dto.MemberDTO;
import com.kh.replay.global.member.model.service.MemberService;

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
	public ResponseEntity<ResponseData<Map<String,String>>> memberlogin(@Valid @RequestBody LocalDTO local) {
		Map<String,String> loginResponse = memberService.memberLogin(local);
		
		
		return ResponseData.ok(loginResponse, "로그인에 성공하셨습니다.");
	}

	@PostMapping("/logout")
	public ResponseEntity<ResponseData<String>> memberlogout(@Valid @RequestBody LocalDTO local ) {
		
		memberService.memberLogout(local);
	
		
		
		
		return ResponseData.ok(null, "로그아웃 되었습니다.");
	}
	
	
	@PutMapping
	public ResponseEntity<ResponseData<ChangePasswordDTO>> changePassword(@RequestBody ChangePasswordDTO password){
		memberService.changePassword(password);
		
		
		
		return ResponseData.created(password, "비밀번호가 변경되었습니다.");
	}
	
	@GetMapping
	public ResponseEntity<ResponseData<Map<String, Object>>> findAllMember(@RequestParam("memberId") String memberId){
		 Map<String, Object> memberInfo =memberService.findAllMember(memberId);
		return ResponseData.ok(memberInfo, "조회에 성공하셨습니다.");
	}
	
	@PatchMapping
	public ResponseEntity<ResponseData<Map<String, Object>>> changeInfo(@RequestBody MemberDTO member){
		
		Map<String, Object> memberInfo =memberService.changeInfo(member);
		
		return ResponseData.ok(memberInfo , "회원정보 수정에 성공하셨습니다.");
		
	}
	@DeleteMapping
	public ResponseEntity<ResponseData<String>> withdrawMember(@RequestBody LocalDTO local)
	{
		memberService.withdrawMember(local);
		
		
				return ResponseData.ok(null, "삭제에 성공하셨습니다.");
		
	}
	

}
