package com.kh.replay.auth.member.model.service;



import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.auth.member.model.dao.MemberMapper;
import com.kh.replay.auth.member.model.dto.ChangePasswordDTO;
import com.kh.replay.auth.member.model.dto.MemberDTO;
import com.kh.replay.auth.member.model.vo.CustomUserDetails;
import com.kh.replay.auth.token.model.dao.TokenMapper;
import com.kh.replay.auth.token.model.service.TokenService;
import com.kh.replay.global.exception.CustomAuthenticationException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service	
@Slf4j
public class MemberServiceImpl implements MemberService{
	private final MemberMapper memberMapper;
	private final UserDetailsServiceImpl user;
	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder;
	private final TokenMapper tokenMapper;
	@Override
	public Map<String,String> memberLogin(@Valid LocalDTO local) {

		Authentication auth = null;
		try {
			
	auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(local.getMemberDto().getEmail(),local.getPassword()));
		}catch(AuthenticationException e) {
			throw new  CustomAuthenticationException("아이디 또는 비밀번호를 확인해주세요.");
		}
		
	CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
	
//	//토큰 삭제
//	tokenMapper.memberLogout(user.getMemberName());
	
	//토큰 발급
	
	Map<String,String> loginResponse =tokenService.generateToken(user.getMemberName());
	
	loginResponse.put("email",user.getUsername());
	loginResponse.put("memberId",user.getMemberName());
	loginResponse.put("password", user.getPassword());
	loginResponse.put("role", user.getAuthorities().toString());
	
	
	
	return loginResponse;
	}
	@Override
	public void changePassword(ChangePasswordDTO password) {
		//현재 인증된 사용자의 정보 뽑아오기
				Authentication auth =SecurityContextHolder.getContext().getAuthentication();
				CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
				
				String currentPassword = password.getCurrentPassword();
				String encodedPassword = user.getPassword();
				
				if(!passwordEncoder.matches(currentPassword,encodedPassword)) {
					throw new CustomAuthenticationException(encodedPassword);
				}
				String newPassword = passwordEncoder.encode(password.getNewPassword());
				
				Map<String,String> changeRequest = Map.of("email",user.getUsername(),
														  "newPassword", newPassword);
				
				memberMapper.changePassword(changeRequest);
		
	}
	@Override
	public void memberLogout(LocalDTO local) {
		
		tokenMapper.memberLogout(local.getMemberDto().getMemberId());
		
		
	}
	@Override
	public Map<String, Object> findAllMember(String memberId) {
		
		Map<String, Object> result =memberMapper.findAllMember(memberId);
		
		
		return result;
	}
	@Transactional
	@Override
	public Map<String, Object> changeInfo(MemberDTO member) {
		int result = memberMapper.changeInfo(member);
//		if(result ==0) {
//			throw new 
//		}
		 Map<String, Object> updateMember =memberMapper.findAllMember(member.getMemberId());
		return  updateMember;
		
		
	}
	@Override
	public void withdrawMember(LocalDTO local) {
		String memberId = local.getMemberDto().getMemberId();
		
		Map<String,String> userInfo =memberMapper.loadUser(local.getMemberDto().getMemberId());
		
		String userPassword =userInfo.get("password");
		
		
		if(!passwordEncoder.matches(local.getPassword(), userPassword)) {
			throw new CustomAuthenticationException("비밀번호가 일치하지 않습니다.");
		}
		memberMapper.wirhdrawMember(memberId);
		
		tokenMapper.memberLogout(memberId);
		
	}

	}
