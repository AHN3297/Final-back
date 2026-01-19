package com.kh.replay.global.auth.local.model.dto;

import com.kh.replay.global.member.model.dto.MemberDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LocalDTO {
	
	@Valid
	private MemberDTO memberDto;
	
	@NotBlank(message="비밀번호를 입력해주세요.")
	//@Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",message="비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다.")
	private String password;
	
	@NotBlank(message="memberId는 필수입니다.")
	@Pattern(regexp="^#.*", message = "memberId는 #으로 시작해야 합니다")
	private String memberId;
	//인코딩된 비밀번호

	

}

