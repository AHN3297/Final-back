package com.kh.replay.global.auth.local.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LocalDTO {
	@NotBlank(message="memberId는 필수입니다.")
	@Pattern(regexp="^#.*", message = "memberId는 #으로 시작해야 합니다")
	private String memberId;
	
	@NotBlank(message="이메일을 입력해주세요.")
	@Pattern(regexp="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$" , message="올바른 이메일 형식이 아닙니다.")
	private String email;
	
	@NotBlank(message="비밀번호를 입력해주세요.")
	@Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",message="비밀번호는 8자 이상, 영문/숫자/특수문자를 포함해야 합니다.")
	private String password;
	
	@NotBlank(message="이름을 입력해주세요.")
	@Size(min = 2, max = 20, message = "별명은 2~20자 사이여야 합니다.")
	private String name;
	
	@NotBlank(message="별명을 입력해주세요.")
	@Size(min= 2,max=20,message="별명은 2~20자 사이여야 합니다." )
	private String nickName;
	
	@NotBlank(message="전화번호를 입력해주세요.")
	@Pattern(regexp="^01[0-9]-\\d{3,4}-\\d{4}$",message="올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
	private String phone;

}
