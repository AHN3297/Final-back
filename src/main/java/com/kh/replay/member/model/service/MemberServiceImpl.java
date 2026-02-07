package com.kh.replay.member.model.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.auth.oauth.model.dao.SocialMapper;
import com.kh.replay.auth.token.model.dao.TokenMapper;
import com.kh.replay.auth.token.model.service.TokenService;
import com.kh.replay.global.exception.CustomAuthenticationException;
import com.kh.replay.member.model.dao.MemberMapper;
import com.kh.replay.member.model.dto.ChangePasswordDTO;
import com.kh.replay.member.model.dto.GenreDTO;
import com.kh.replay.member.model.dto.MemberDTO;
import com.kh.replay.member.model.dto.MemberInfoDTO;
import com.kh.replay.member.model.dto.MemberUpdateRequest;
import com.kh.replay.member.model.vo.CustomUserDetails;
import com.kh.replay.member.model.vo.MemberVO;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {
	private final MemberMapper membermapper;
	private final UserDetailsServiceImpl user;
	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	private final PasswordEncoder passwordEncoder;
	private final TokenMapper tokenMapper;
	private final SocialMapper socialMapper;

	@Override
	public Map<String, String> memberLogin(@Valid LocalDTO local) {

		Authentication auth = null;
		try {

			auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(local.getMemberDto().getEmail(), local.getPassword()));

		} catch (AuthenticationException e) {
			throw new CustomAuthenticationException("아이디 또는 비밀번호를 확인해주세요.");
		}

		CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

		String role = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("");

		// 토큰 발급

		Map<String, String> loginResponse = tokenService.generateToken(user.getUsername(), role);

		loginResponse.put("memberId", user.getUsername());
		loginResponse.put("password", user.getPassword());
		loginResponse.put("role", role);

		return loginResponse;
	}

	@Override
	public void changePassword(ChangePasswordDTO password) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

		String currentPassword = password.getCurrentPassword();
		String encodedPassword = user.getPassword();

		if (!passwordEncoder.matches(currentPassword, encodedPassword)) {
			throw new CustomAuthenticationException("현재 비밀번호가 일치하지 않습니다.");
		}

		String newPassword = passwordEncoder.encode(password.getNewPassword());

		String username = user.getUsername();

		if (username == null) {
			throw new IllegalStateException("사용자 정보를 찾을 수 없습니다.");
		}

		Map<String, String> changeRequest = Map.of("memberId", username, "newPassword", newPassword);
		
		membermapper.changePassword(changeRequest);
		
	}

	@Override
	public void memberLogout(LocalDTO local) {

		tokenMapper.memberLogout(local.getMemberDto().getMemberId());
		

	}

	@Override
	public MemberInfoDTO findAllInfo(String memberId) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

	    if (!memberId.equals(user.getUsername())) {
	        throw new CustomAuthenticationException("사용자 정보가 일치하지 않습니다.");
	    }

	    List<MemberInfoDTO> rows = membermapper.findAllInfo(memberId);

	    if (rows == null || rows.isEmpty()) {
	        throw new CustomAuthenticationException("회원 정보를 찾을 수 없습니다.");
	    }

	
	    MemberInfoDTO result = rows.get(0);

	   
	    List<GenreDTO> genres =
	    	    rows.stream()
	    	        .flatMap(r -> {
	    	            List<GenreDTO> g = r.getGenreDto();
	    	            return g == null ? Stream.empty() : g.stream();
	    	        })
	    	        .filter(genre -> genre.getGenreId() != null)
	    	        .toList();

	    	result.setGenreDto(genres.isEmpty() ? null : genres);
			return result;

	}



	@Transactional
	@Override
	public List<MemberInfoDTO> changeInfo(MemberUpdateRequest request) {
	    membermapper.changeInfo(request);
	    
	    if (request.getGenres() != null) {
	        membermapper.deleteMemberGenres(request.getMemberId());
	        
	        if (!request.getGenres().isEmpty()) {
	            membermapper.insertMemberGenres(
	                request.getMemberId(),
	                request.getGenres()
	            );
	        }
	    }
	    
	    return membermapper.findAllInfo(request.getMemberId());
	}


	@Override
	public void withdrawMember(LocalDTO local) {
	    String memberId = local.getMemberDto().getMemberId();

	    Map<String, String> userInfo =
	        membermapper.findByMemberId(memberId);

	    if (userInfo == null) {
	        throw new CustomAuthenticationException("회원 정보를 찾을 수 없습니다.");
	    }

	    String userPassword = userInfo.get("PASSWORD");

	    if (userPassword == null) {
	        throw new CustomAuthenticationException("비밀번호 기반 회원이 아닙니다.");
	    }

	    if (!passwordEncoder.matches(local.getPassword(), userPassword)) {
	        throw new CustomAuthenticationException("비밀번호가 일치하지 않습니다.");
	    }

	    membermapper.withdrawMember(memberId);
	    tokenMapper.memberLogout(memberId);
	}


	@Override
	public void withdrawSocial(MemberVO member) {

		String memberId = member.getMemberId();
		Map<String, String> socialUser = membermapper.loadSocialUser(memberId);

		String socialProvider = socialUser.get("PROVIDER");
		if (socialProvider.isEmpty()) {
			throw new CustomAuthenticationException("소셜 로그인 회원이 아닙니다.");
		}

		membermapper.withdrawSocial(memberId);
		tokenMapper.memberLogout(memberId);

	}
	@Override
	public List<GenreDTO> findAllGenres() {
	    return membermapper.findAllGenres();
	}


}
