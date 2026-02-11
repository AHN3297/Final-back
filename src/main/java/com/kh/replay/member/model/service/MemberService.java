package com.kh.replay.member.model.service;

import java.util.List;
import java.util.Map;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.member.model.dto.ChangePasswordDTO;
import com.kh.replay.member.model.dto.GenreDTO;
import com.kh.replay.member.model.dto.MemberDTO;
import com.kh.replay.member.model.dto.MemberInfoDTO;
import com.kh.replay.member.model.dto.MemberUpdateRequest;
import com.kh.replay.member.model.vo.MemberVO;

import jakarta.validation.Valid;



public interface MemberService {


	Map<String, String> memberLogin(@Valid LocalDTO local);
	void changePassword(ChangePasswordDTO password);
	void memberLogout(LocalDTO local);
	List<MemberInfoDTO> changeInfo(MemberUpdateRequest request);
	void withdrawMember(LocalDTO local);
	void withdrawSocial(MemberVO member);
	MemberInfoDTO findAllInfo(String memberId);
	List<GenreDTO> findAllGenres();


	
}
