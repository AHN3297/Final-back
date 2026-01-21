package com.kh.replay.global.member.model.service;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.replay.global.member.model.dao.MemberMapper;
import com.kh.replay.global.member.model.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Service
@Slf4j

public class UserDetailsServiceImpl implements UserDetailsService {
//AuthnticaionManager가 실질적으로 사용자의 정보를 조회할 때 메소드를 호출하는 클래스
	private final MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//localDTO 타입으로 하면 안에있는 memberDTO랑 매칭이 안돼서 null들어옴 때문에 Map으로 받음 
		//여기서 근데 반환 타입이 userDetails여서 따로 만들어서 Map에서 뽑아서 사용
		Map<String,String> local = memberMapper.loadUser(username);
		if(local ==null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}
		
		return  (UserDetails) CustomUserDetails.builder().username(local.get("MEMBERID"))
													 	 .password(local.get("PASSWORD"))
													 	 .memberName(local.get("MEMBERNAME"))
														 .authorities(Collections.singletonList(new SimpleGrantedAuthority(local.get("ROLE"))))
														 .build();
	
	}
	
}
