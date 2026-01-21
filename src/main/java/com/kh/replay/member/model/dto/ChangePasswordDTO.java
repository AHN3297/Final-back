package com.kh.replay.member.model.dto;

import lombok.Data;
import lombok.Setter;

@Data
public class ChangePasswordDTO {

	private String memberId;	
	
	private String currentPassword;
	
	private String newPassword;
}
