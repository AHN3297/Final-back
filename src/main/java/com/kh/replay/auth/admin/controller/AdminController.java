package com.kh.replay.auth.admin.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.auth.admin.model.service.AdminService;
import com.kh.replay.global.common.ResponseData;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth/admin")
public class AdminController {
	private final AdminService adminService;

	@GetMapping
	public ResponseEntity<ResponseData<Map<String,Object>>> memberList(@RequestParam(name="page",defaultValue ="1") int page , @RequestParam(name="size",defaultValue= "10") int size){
		
		Map<String,Object> result = adminService.memberList(page,size);
		
		
		
		
		return ResponseData.ok(result,"회원 목록 조회 성공");
		
	}
	@GetMapping("/dashboard")
	public ResponseEntity<ResponseData<?>> getDashboardSummary(@RequestParam(name ="totalAccount")int totalAccount,@RequestParam(name="user") int user,
															   @RequestParam(name="admins") int admins, @RequestParam(name="withdrawnAccounts") int withdrawnAccounts,
															   @RequestParam(name="asOf")Date asOf){
																
		
		adminService.getDashboardSummary(totalAccount,admins,user,asOf,withdrawnAccounts);
		
		
		return null; 
		
	}
}
