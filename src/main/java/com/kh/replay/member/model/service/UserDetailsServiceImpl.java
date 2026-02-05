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
		// email로 사용자 조회 (로그인용 - 일반 로그인만)
		Map<String, String> local = membermapper.loadByMemberEmail(username);

		if (local == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}

		return createUserDetails(local, true); // 일반 로그인
	}

	// JWT 검증 (memberId로 조회 - 일반/소셜 모두)
	public UserDetails loadUserByMemberId(String memberId) throws UsernameNotFoundException {
		Map<String, String> user = membermapper.findUserByMemberId(memberId);

		if (user == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		}

		// PASSWORD가 있으면 일반 로그인, 없으면 소셜 로그인
		boolean isLocalUser = user.get("PASSWORD") != null;

		return createUserDetails(user, isLocalUser);
	}

	private UserDetails createUserDetails(Map<String, String> userData, boolean isLocalUser) {
		String memberId = userData.get("MEMBER_ID");
		String password = userData.get("PASSWORD");
		String role = userData.get("ROLE");
		String name = userData.get("MEMBER_NAME");
		String email = userData.get("EMAIL");

		if (memberId == null) {
			throw new IllegalStateException("MEMBER_ID가 없습니다.");
		}

		// 일반 로그인: password 필수
		if (isLocalUser && password == null) {
			throw new IllegalStateException("일반 로그인 사용자는 PASSWORD가 필요합니다.");
		}

		// 소셜 로그인: password는 빈 문자열
		if (!isLocalUser) {
			log.debug("소셜 로그인 사용자: memberId={}", memberId);
			password = ""; 
		}

		CustomUserDetails userDetails = CustomUserDetails.builder().username(memberId).password(password)
				.memberName(name != null ? name : email)
				.authorities(Collections.singletonList(new SimpleGrantedAuthority(role != null ? role : "ROLE_USER")))
				.build();

		return userDetails;
	}
}