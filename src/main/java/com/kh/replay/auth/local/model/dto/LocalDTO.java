package com.kh.replay.auth.local.model.dto;



import com.kh.replay.member.model.dto.MemberDTO;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
@Builder
@Data
public class LocalDTO {
	
	@Valid
	private MemberDTO memberDto;
	
//	@NotBlank(message="비밀번호를 입력해주세요.")
	//@Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",message="비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다.")
	private String password;
	
	
}

