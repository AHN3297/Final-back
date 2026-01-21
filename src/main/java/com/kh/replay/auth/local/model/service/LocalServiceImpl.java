package com.kh.replay.auth.local.model.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.replay.auth.local.model.dao.LocalMapper;
import com.kh.replay.auth.local.model.dto.LocalDTO;
import com.kh.replay.global.exception.DuplicateException;
import com.kh.replay.global.exception.MemberJoinException;
import com.kh.replay.global.member.model.vo.MemberVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocalServiceImpl implements LocalService {
	private final LocalMapper localMapper;
	private final PasswordEncoder passwordEncoder;

	@Transactional // 여러 개의 디비 작업을 한번에 처리
	@Override
	public int signUp(LocalDTO local) {

//      localDto.getMemberDto().getEmail() // 이거를 가따가 DB에 가야죠

//if(localDto.equals(localDto.getMemberDto().getEmail())) {
//throw new DuplicateException("이미 존재하는 이메일입니다.");
//}
//if(localDto.equals(localDto.getMemberDto().getNickName())) {
//
//     throw new DuplicateException("이미 존재하는 NickName입니다.");
//  }if(localDto.equals(localDto.getMemberDto().getPhone())) {
//
//     throw new DuplicateException("이미 존재하는 핸드폰 번호입니다.");
//  }

		String encodedPassword = passwordEncoder.encode(local.getPassword());

		// 암호화된 비밀번호 로컬에 저장

		local.setPassword(encodedPassword);
		log.info("{}",local.getMemberDto());
		MemberVO member = MemberVO.builder().memberId(local.getMemberDto().getMemberId())
				.email(local.getMemberDto().getEmail()).name(local.getMemberDto().getName())
				.nickName(local.getMemberDto().getNickName()).gender(local.getMemberDto().getGender())
				.mbti(local.getMemberDto().getMbti()).phone(local.getMemberDto().getPhone())
				.createdAt(local.getMemberDto().getCreatedAt()).updatedAt(local.getMemberDto().getUpdatedAt())
				.genre(local.getMemberDto().getGenre()).role("ROLE_USER").job(local.getMemberDto().getJob())
				.status("Y").build();

		// 회원 공통 정보 insert
		int result = localMapper.insertMember(member);

		if (result <= 0) {
			throw new MemberJoinException("회원가입에 실패했습니다.");
		}

		result = localMapper.signUp(local);

		if (result <= 0) {
			throw new MemberJoinException("로컬 인증 정보 등록에 실패했습니다.");
		}

		return result;

	}

}