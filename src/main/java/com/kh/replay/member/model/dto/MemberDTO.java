package com.kh.replay.member.model.dto;

import java.sql.Date;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

	@NotBlank(message = "memberId는 필수입니다.")
	@Size(max = 20, message = "memberId는 최대 20자입니다.")
	@Pattern(regexp = "^#[^\\s]{1,19}$", message = "memberId는 #으로 시작하며 공백 없이 20자 이내여야 합니다.")
	private String memberId;

	@NotBlank(message = "이메일을 입력해주세요.")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@Pattern(regexp = "^(남성|여성|기타)$", message = "올바른 성별 값이 아닙니다.")
	@NotBlank(message = "성별을 선택해주세요.")
	private String gender;

	@NotBlank(message = "mbti를 선택해주세요.")

	private String mbti;

	private Date updatedAt;

	private Date createdAt;

	@NotBlank(message = "이름을 입력해주세요.")
	@Size(min = 2, max = 20, message = "별명은 2~20자 사이여야 합니다.")
	private String name;

	@NotBlank(message = "별명을 입력해주세요.")
	@Size(min = 2, max = 20, message = "별명은 2~20자 사이여야 합니다.")
	private String nickName;

	@NotBlank(message = "전화번호를 입력해주세요.")
	@Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "올바른 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
	private String phone;

	@Size(max = 30, message = "직업은 30자 이내로 입력해주세요.")
	@NotBlank(message = "직업을 입력해주세요.")
	private String job;

	private String role;

	private String status;

}