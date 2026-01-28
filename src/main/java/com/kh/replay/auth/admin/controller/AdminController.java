package com.kh.replay.auth.admin.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.replay.auth.admin.model.dto.DashboardSummaryDTO;
import com.kh.replay.auth.admin.model.dto.MemberDetailDTO;
import com.kh.replay.auth.admin.model.dto.MemberStatusRatio;
import com.kh.replay.auth.admin.model.service.AdminService;
import com.kh.replay.global.common.ResponseData;
import com.kh.replay.member.model.dto.MemberDTO;

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
	public ResponseEntity<ResponseData<DashboardSummaryDTO>> getDashboardSummary(){
																
		
		DashboardSummaryDTO response = adminService.getDashboardSummary();
		
		
		return ResponseData.ok(response,"조회 성공"); 
		
	}
	
	@GetMapping("/dashboard/statistics")
	public ResponseEntity<ResponseData<MemberStatusRatio>> getMemberStatusRatio(){
		
		MemberStatusRatio memberStatus = adminService.getMemberStatusRatio();
		
		
		
		
	return ResponseData.ok(memberStatus, "회원 상태 비율 조회 성공");
		
	}
	@GetMapping("/memberDetails")
	public   ResponseEntity<ResponseData<MemberDetailDTO>> getMemberDetails(@RequestParam (name = "memberId") String memberId ){
		
		MemberDetailDTO response =adminService.getMemberDetails(memberId);
		
		
		
		return ResponseData.ok(response, "회원 상세 조회 성공");
			
	}
	
	@PatchMapping
	public ResponseEntity<ResponseData<?>> ChangePermissions(@RequestBody MemberDTO member  ){
		
		
		adminService.ChangePermissions(member);
		
		
		
		return null;
		
	}
	
}
