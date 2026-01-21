package com.kh.replay.member.model.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.replay.member.model.dao.MemberMapper;
import com.kh.replay.member.model.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Service
@Slf4j

public class UserDetailsServiceImpl implements UserDetailsService {
	private final MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Map<String,String> local = memberMapper.loadUser(username);
		if(local ==null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}
		
		return  (UserDetails) CustomUserDetails.builder().username(local.get("MEMBER_ID"))
													 	 .password(local.get("PASSWORD"))
													 	 .memberName(local.get("MEMBER_NAME"))
														 .authorities(Collections.singletonList(new SimpleGrantedAuthority(local.get("ROLE"))))
														 .build();
	
	}
	
}
