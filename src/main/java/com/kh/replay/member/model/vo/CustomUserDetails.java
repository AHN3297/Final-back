package com.kh.replay.member.model.vo;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.Value;
@Value
@Builder
//인증된 사용자의 정보
public class CustomUserDetails implements UserDetails {
	private String username;
	private String password;
	private String memberName;
	private Collection<? extends GrantedAuthority> authorities;
	
	
}
