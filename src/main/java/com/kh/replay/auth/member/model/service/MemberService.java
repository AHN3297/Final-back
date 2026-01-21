package com.kh.replay.auth.member.model.service;

import java.util.Map;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.auth.member.model.dto.ChangePasswordDTO;
import com.kh.replay.auth.member.model.dto.MemberDTO;

import jakarta.validation.Valid;



public interface MemberService {


	Map<String, String> memberLogin(@Valid LocalDTO local);
	void changePassword(ChangePasswordDTO password);
	void memberLogout(LocalDTO local);
	Map<String, Object> findAllMember(String memberId);
	Map<String, Object> changeInfo(MemberDTO member);
	void withdrawMember(LocalDTO local);


	
}
