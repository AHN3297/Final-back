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
	private final MemberMapper membermapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    
	    
	    Map<String, String> local;
	    
	    // JWT로  email로 사용자 조회(로그인)
	    local = membermapper.loadByMemberEmail(username);
	    if(local==null) {
	    	throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
	    }
	return createUserDetails(local);    
	}
	    
	    //JWT 검증 (memberId로 조회)
	    public UserDetails loadUserByMemberId(String memberId) throws UsernameNotFoundException{	    
	    	Map<String,String> local = membermapper.findByMemberId(memberId);
	    	if(local ==null) {
	    		throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
	    		}
	    	return createUserDetails(local);
	    }
	    
	    private UserDetails createUserDetails(Map<String,String> local) {
	    
	  
	    String memberId = local.get("MEMBER_ID");
	    String password = local.get("PASSWORD");
	    String role = local.get("ROLE");
	    String name = local.get("NAME");
	    String email = local.get("EMAIL");
	    
	    if(memberId == null) {
	        throw new IllegalStateException("MEMBER_ID가 없습니다.");
	    }
	    if(password == null) {
	    	throw new IllegalStateException("PASSWORD가 없습니다.");
	    }
	    
	    CustomUserDetails userDetails = CustomUserDetails.builder()
	            .username(memberId)  // MEMBER_ID
	            .password(password)
	            .memberName(name != null ? name : email)
	            .authorities(Collections.singletonList(
	                new SimpleGrantedAuthority(role != null ? role: "ROLE_USER")
	            ))
	            .build();
	    
	    return userDetails;
	
}
	}
