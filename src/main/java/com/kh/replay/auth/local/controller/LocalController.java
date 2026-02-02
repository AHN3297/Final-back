package com.kh.replay.auth.local.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.auth.local.model.service.LocalService;
import com.kh.replay.global.common.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/auth")
public class LocalController {
	private final LocalService localService;
	
	@PostMapping("/signUp")
	public ResponseEntity<ResponseData<String>> signUp(@Valid @RequestBody LocalDTO local){
	
		localService.signUp(local);
		
		return ResponseData.created(null, "회원가입에 성공했습니다.");
		
	}
	
}