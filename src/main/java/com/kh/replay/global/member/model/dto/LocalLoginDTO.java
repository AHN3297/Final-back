package com.kh.replay.global.member.model.dto;

import com.kh.replay.global.auth.local.model.dto.LocalDTO;

import jakarta.validation.Valid;
import lombok.Data;

@Data 
public class LocalLoginDTO {
	@Valid
	private MemberDTO memberDto;
	private LocalDTO localDto;

}
