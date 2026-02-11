package com.kh.replay.auth.local.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.auth.local.model.dao.LocalMapper;
import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.global.exception.MemberJoinException;
import com.kh.replay.member.model.vo.MemberVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalServiceImpl implements LocalService {
	private final LocalMapper localmapper;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	@Override
	public int signUp(LocalDTO local) {

		String encodedPassword = passwordEncoder.encode(local.getPassword());

		// 암호화된 비밀번호 로컬에 저장

		local.setPassword(encodedPassword);

		MemberVO member = MemberVO.builder().memberId(local.getMemberDto().getMemberId())
				.email(local.getMemberDto().getEmail()).name(local.getMemberDto().getName())
				.nickName(local.getMemberDto().getNickName()).gender(local.getMemberDto().getGender())
				.mbti(local.getMemberDto().getMbti()).phone(local.getMemberDto().getPhone())
				.role(local.getMemberDto().getRole() != null ? local.getMemberDto().getRole() : "ROEL_USER")
				.job(local.getMemberDto().getJob()).build();

		// 회원 공통 정보 insert
		int result = localmapper.insertMember(member);

		local.getMemberDto().setMemberId(member.getMemberId());

		if (result <= 0) {
			throw new MemberJoinException("회원가입에 실패했습니다.");
		}
		;
		
		for (Long genreId : local.getGenreIds()) {
			localmapper.insertOneMemberGenre(local.getMemberDto().getMemberId(), genreId);
		}

		int result2 = localmapper.signUp(local);

		if (result2 <= 0) {
			throw new MemberJoinException("로컬 인증 정보 등록에 실패했습니다.");
		}

		return result;

	}

}