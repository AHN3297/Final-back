package com.kh.replay.global.member.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.global.member.model.dto.LocalLoginDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/members/")
@Slf4j
@RequiredArgsConstructor
public class MemberController {
	@GetMapping("/login")
	public ResponseEntity<Map<String,String>> login(LocalLoginDTO localLogin) {
		
		
		
		
		
		return null;
	}

}
