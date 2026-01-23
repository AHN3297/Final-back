package com.kh.replay.member.model.service;



import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.auth.token.model.dao.TokenMapper;
import com.kh.replay.auth.token.model.service.TokenService;
import com.kh.replay.global.exception.CustomAuthenticationException;
import com.kh.replay.member.model.dao.MemberMapper;
import com.kh.replay.member.model.dto.ChangePasswordDTO;
import com.kh.replay.member.model.dto.MemberDTO;
import com.kh.replay.member.model.vo.CustomUserDetails;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service	
@Slf4j
public class MemberServiceImpl implements MemberService{
	private final MemberMapper membermapper;
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
	
	
	//토큰 발급
	
	Map<String,String> loginResponse =tokenService.generateToken(user.getUsername());
	
	loginResponse.put("memberId",user.getUsername());
	loginResponse.put("password", user.getPassword());
	loginResponse.put("role", user.getAuthorities().toString());
	
	
	
	return loginResponse;
	}
	@Override
	public void changePassword(ChangePasswordDTO password) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

	    String currentPassword = password.getCurrentPassword();
	    String encodedPassword = user.getPassword();

	    
	    if(!passwordEncoder.matches(currentPassword, encodedPassword)) {
	        throw new CustomAuthenticationException("현재 비밀번호가 일치하지 않습니다.");
	    }
	    
	    String newPassword = passwordEncoder.encode(password.getNewPassword());
	    
	    String username = user.getUsername();
	    
	    if(username == null) {
	        throw new IllegalStateException("사용자 정보를 찾을 수 없습니다.");
	    }
	    
	    Map<String, String> changeRequest = Map.of(
	        "memberId", username,
	        "newPassword", newPassword
	    );
	}
	@Override
	public void memberLogout(LocalDTO local) {
		
		tokenMapper.memberLogout(local.getMemberDto().getMemberId());
		
		
	}
	@Override
	public Map<String, Object> findAllMember(String memberId) {
		
		Map<String, Object> result =membermapper.findAllMember(memberId);
		
		
		return result;
	}
	@Transactional
	@Override
	public Map<String, Object> changeInfo(MemberDTO member) {
		int result = membermapper.changeInfo(member);
		 Map<String, Object> updateMember =membermapper.findAllMember(member.getMemberId());
		return  updateMember;
		
		
	}
	@Override
	public void withdrawMember(LocalDTO local) {
		String memberId = local.getMemberDto().getMemberId();
		
		Map<String,String> userInfo =membermapper.loadUser(local.getMemberDto().getEmail());
		
		String userPassword =userInfo.get("PASSWORD");
		
		if(!passwordEncoder.matches(local.getPassword(), userPassword)) {
			log.info("{},{}" ,local.getPassword() , userPassword);
			throw new CustomAuthenticationException("비밀번호가 일치하지 않습니다.");
		}
		membermapper.withdrawMember(memberId);
		
		tokenMapper.memberLogout(memberId);
		
	}

	}
