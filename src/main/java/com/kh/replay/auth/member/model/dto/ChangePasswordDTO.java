package com.kh.replay.auth.member.model.dto;

import lombok.Setter;
import lombok.Value;

@Value
@Setter
public class ChangePasswordDTO {

	private String currentPassword;
	
	private String newPassword;
}
